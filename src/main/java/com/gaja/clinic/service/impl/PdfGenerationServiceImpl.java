package com.gaja.clinic.service.impl;

import com.gaja.clinic.entity.Bill;
import com.gaja.clinic.entity.Patient;
import com.gaja.clinic.entity.Prescription;
import com.gaja.clinic.entity.PrescriptionItem;
import com.gaja.clinic.model.ClinicSettings;
import com.gaja.clinic.service.PdfGenerationService;
import com.gaja.clinic.service.SettingsService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private static final Color RED_PRIMARY  = new Color(229, 57, 53);
    private static final Color RED_DEEP     = new Color(183, 28, 28);
    private static final Color RED_LIGHT    = new Color(255, 205, 210);   // watermark / soft
    private static final Color WHITE        = Color.WHITE;
    private static final Color TEXT_DARK    = new Color(26, 31, 46);
    private static final Color TEXT_MUTED   = new Color(92, 101, 120);
    private static final Color BLACK        = Color.BLACK;
    private static final Color LIGHT_GRAY   = new Color(220, 220, 220);

    private final SettingsService settingsService;

    @Value("${app.static.filesystem-path:src/main/resources/static}")
    private String staticFilesystemPath;

    private Path staticRoot;

    @PostConstruct
    void initStaticRoot() throws IOException {
        staticRoot = Path.of(System.getProperty("user.dir"), staticFilesystemPath).toAbsolutePath().normalize();
        Files.createDirectories(staticRoot.resolve("images"));
        Files.createDirectories(staticRoot.resolve("uploads/prescription"));
    }

    // ── Footer reserved height ───────────────────────────────────────────────
    private static final float FOOTER_RESERVED_HEIGHT = 90f;
    private static final float FOOTER_Y_START         = 85f;

    // ── Page margins ─────────────────────────────────────────────────────────
    private static final float MARGIN_LEFT   = 36f;
    private static final float MARGIN_RIGHT  = 36f;
    private static final float MARGIN_TOP    = 30f;

    // ============================================================
    @Override
    public byte[] generatePrescriptionPdf(Prescription prescription, Bill bill) {
        try {
            ClinicSettings clinic = settingsService.getSettings();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, FOOTER_RESERVED_HEIGHT);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new LetterheadPageEvent(clinic));
            document.open();

            // ── 1. Header (logo | name | divider | phone) ──────────────────
            document.add(buildTopHeader(clinic));

            // ── 2. Thin red separator ───────────────────────────────────────
            document.add(buildRedLine());

            // ── 3. Doctor info + consulting hours row ──────────────────────
            document.add(buildDoctorHoursRow(clinic));

            // ── 4. Red separator ────────────────────────────────────────────
            document.add(buildRedLine());

            // ── 5. Spacer ────────────────────────────────────────────────────
            document.add(new Paragraph(" ", font(5, Font.NORMAL, WHITE)));

            // ── 6. Patient info row (name, age, sex, date) ──────────────────
            document.add(buildPatientInfoRow(prescription));

            // ── 7. Vitals block (right side, stacked) + body area ──────────
            document.add(buildVitalsAndBodyTable(prescription));

            // ── 8. Medicines ─────────────────────────────────────────────────
            document.add(buildMedicineTable(prescription.getItems()));

            // ── 8b. Doctor's Notes / Advice ─────────────────────────────────
            if (prescription.getNotes() != null && !prescription.getNotes().isBlank()) {
                document.add(new Paragraph(" ", font(4, Font.NORMAL, WHITE)));
                Paragraph notesHeader = new Paragraph("Doctor's Notes / Advice", font(9, Font.BOLD, TEXT_DARK));
                notesHeader.setSpacingAfter(4f);
                document.add(notesHeader);
                Paragraph notesBody = new Paragraph(prescription.getNotes().trim(), font(8.5f, Font.NORMAL, TEXT_DARK));
                notesBody.setSpacingAfter(6f);
                document.add(notesBody);
            }

            // ── 9. Billing summary ───────────────────────────────────────────
            document.add(new Paragraph(" ", font(6, Font.NORMAL, WHITE)));
            document.add(buildBillingSection(bill));

            document.close();
            return out.toByteArray();

        } catch (Exception ex) {
            log.error("PDF generation failed for prescription {}", prescription.getId(), ex);
            throw new IllegalStateException("PDF generation failed: " + ex.getMessage(), ex);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Page event — draws watermark logo + pinned footer on every page
    // ════════════════════════════════════════════════════════════════════════
    private class LetterheadPageEvent extends PdfPageEventHelper {

        private final ClinicSettings clinic;

        LetterheadPageEvent(ClinicSettings clinic) {
            this.clinic = clinic;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                drawWatermark(writer, document);
                drawPinnedFooter(writer, document);
            } catch (Exception ex) {
                log.warn("Page event rendering failed: {}", ex.getMessage());
            }
        }

        // Faint logo watermark in the body area
        private void drawWatermark(PdfWriter writer, Document document) {
            try {
                Image logo = tryLoadImage(clinic.getLogoPath(), 260, 260);
                if (logo == null) return;
                PdfContentByte canvas = writer.getDirectContentUnder();
                logo.setAbsolutePosition(
                        (document.getPageSize().getWidth() - logo.getScaledWidth()) / 2f,
                        (document.getPageSize().getHeight() - logo.getScaledHeight()) / 2f - 30f);
                // Use a very low opacity by setting transparency
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.08f);
                canvas.setGState(gs);
                canvas.addImage(logo);
                // Reset opacity
                PdfGState gsReset = new PdfGState();
                gsReset.setFillOpacity(1f);
                canvas.setGState(gsReset);
            } catch (Exception e) {
                log.debug("Watermark skipped: {}", e.getMessage());
            }
        }

        // Pinned footer: red bar + address
        private void drawPinnedFooter(PdfWriter writer, Document document) throws DocumentException {
            PdfContentByte canvas = writer.getDirectContent();
            float pageWidth  = document.getPageSize().getWidth();
            float left       = MARGIN_LEFT;
            float usable     = pageWidth - left - MARGIN_RIGHT;
            float currentY   = FOOTER_Y_START;

            // Signature (right)
            Image signature = tryLoadImage(clinic.getDoctorSignaturePath(), 100, 40);
            if (signature != null) {
                signature.setAbsolutePosition(left + usable - signature.getScaledWidth(), currentY + 28f);
                canvas.addImage(signature);
            }
            // Doctor name right-aligned above banner
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,
                    new Phrase(formatDoctorName(clinic), font(9, Font.BOLD, RED_DEEP)),
                    left + usable, currentY + 14f, 0);

            // Red banner
            float bannerH = 22f;
            canvas.setColorFill(RED_PRIMARY);
            canvas.rectangle(left, currentY - bannerH, usable, bannerH);
            canvas.fill();
            String msg = clinic.getPrescriptionFooterMessage();
            if (msg == null || msg.isBlank()) msg = "Wishing You Good Health";
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                    new Phrase(msg, font(10, Font.BOLD, WHITE)),
                    left + usable / 2f, currentY - bannerH + 6f, 0);

            currentY -= bannerH;

            // Address centred below banner
            StringBuilder addr = new StringBuilder();
            if (clinic.getAddress() != null && !clinic.getAddress().isBlank()) addr.append(clinic.getAddress());
            if (clinic.getPhone() != null && !clinic.getPhone().isBlank()) {
                if (!addr.isEmpty()) addr.append("   Ph: ");
                else addr.append("Ph: ");
                addr.append(clinic.getPhone());
            }
            if (!addr.isEmpty()) {
                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                        new Phrase(addr.toString(), font(8, Font.BOLD, RED_DEEP)),
                        left + usable / 2f, currentY - 13f, 0);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Section builders
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Top header: [Logo] | [Clinic Name + Tagline] | vertical divider | [Appointment Phone]
     */
    private PdfPTable buildTopHeader(ClinicSettings clinic) throws DocumentException {
        // 4 columns: logo | name block | divider | phone block
        PdfPTable table = new PdfPTable(new float[]{1.2f, 3.5f, 0.05f, 2f});
        table.setWidthPercentage(100);
        table.setSpacingAfter(0f);

        // Logo cell
        PdfPCell logoCell = noBorderCell();
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Image logo = tryLoadImage(clinic.getLogoPath(), 75, 75);
        if (logo != null) {
            logoCell.addElement(logo);
        } else {
            logoCell.addElement(new Phrase("G", font(28, Font.BOLD, RED_PRIMARY)));
        }
        table.addCell(logoCell);

        // Clinic name + tagline cell
        PdfPCell nameCell = noBorderCell();
        nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        nameCell.setPaddingLeft(6f);
        Paragraph clinicName = new Paragraph(safeUpper(clinic.getClinicName()), font(16, Font.BOLD, RED_DEEP));
        clinicName.setSpacingAfter(2f);
        nameCell.addElement(clinicName);
        String tagline = clinic.getTagline();
        if (tagline != null && !tagline.isBlank()) {
            nameCell.addElement(new Paragraph(tagline, font(10, Font.BOLDITALIC, RED_PRIMARY)));
        }
        table.addCell(nameCell);

        // Vertical red divider cell
        PdfPCell dividerCell = noBorderCell();
        dividerCell.setBorderWidthLeft(2f);
        dividerCell.setBorderColorLeft(RED_PRIMARY);
        dividerCell.setMinimumHeight(60f);
        table.addCell(dividerCell);

        // Appointment phone cell
        PdfPCell apptCell = noBorderCell();
        apptCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        apptCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        apptCell.setPaddingLeft(8f);
        Paragraph apptLabel = new Paragraph("For Appointment Call", font(9, Font.NORMAL, TEXT_DARK));
        apptLabel.setAlignment(Element.ALIGN_CENTER);
        apptCell.addElement(apptLabel);
        if (clinic.getPhone() != null && !clinic.getPhone().isBlank()) {
            Paragraph phone = new Paragraph(clinic.getPhone(), font(12, Font.BOLD, RED_PRIMARY));
            phone.setAlignment(Element.ALIGN_CENTER);
            apptCell.addElement(phone);
        }
        table.addCell(apptCell);

        return table;
    }

    /**
     * Thin full-width red rule.
     */
    private PdfPTable buildRedLine() throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(2f);
        t.setSpacingAfter(2f);
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        c.setBackgroundColor(RED_PRIMARY);
        c.setFixedHeight(2f);
        t.addCell(c);
        return t;
    }

    /**
     * Doctor name / regd / specialization (left) | Consulting hours (right)
     */
    private PdfPTable buildDoctorHoursRow(ClinicSettings clinic) throws DocumentException {
        PdfPTable t = new PdfPTable(new float[]{3f, 2f});
        t.setWidthPercentage(100);
        t.setSpacingBefore(4f);
        t.setSpacingAfter(4f);

        // Left — doctor info
        PdfPCell left = noBorderCell();
        left.setVerticalAlignment(Element.ALIGN_MIDDLE);
        left.addElement(new Paragraph(formatDoctorName(clinic), font(10, Font.BOLD, TEXT_DARK)));
        if (notBlank(clinic.getDoctorRegistrationNo())) {
            left.addElement(new Paragraph("Regd. No : " + clinic.getDoctorRegistrationNo(), font(9, Font.NORMAL, TEXT_DARK)));
        }
        if (notBlank(clinic.getDoctorSpecialization())) {
            left.addElement(new Paragraph(clinic.getDoctorSpecialization(), font(9, Font.BOLD, TEXT_DARK)));
        }
        t.addCell(left);

        // Right — consulting hours
        PdfPCell right = noBorderCell();
        right.setVerticalAlignment(Element.ALIGN_MIDDLE);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        String hours = clinic.getConsultingHours();
        if (notBlank(hours)) {
            Paragraph hoursTitle = new Paragraph("CONSULTING HOURS", font(8, Font.BOLD, TEXT_DARK));
            hoursTitle.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(hoursTitle);
            for (String line : hours.split(",")) {
                Paragraph h = new Paragraph(line.trim(), font(9, Font.BOLD, TEXT_DARK));
                h.setAlignment(Element.ALIGN_RIGHT);
                right.addElement(h);
            }
        }
        t.addCell(right);

        return t;
    }

    /**
     * Patient Name ........................... Age ........ Sex : M / F   Date ......................
     */
    private PdfPTable buildPatientInfoRow(Prescription prescription) throws DocumentException {
        Patient p = prescription.getPatient();

        PdfPTable t = new PdfPTable(new float[]{4f, 1f, 1.5f, 2f});
        t.setWidthPercentage(100);
        t.setSpacingBefore(6f);
        t.setSpacingAfter(0f);

        String name   = p != null ? p.getName() : "";
        String age    = p != null && p.getAge() != null ? String.valueOf(p.getAge()) : "";
        String gender = p != null ? (p.getGender() != null ? p.getGender() : "") : "";
        String date   = prescription.getDateCreated().format(DATE_FMT);

        // Name with dotted underline
        PdfPCell nameCell = noBorderCell();
        Paragraph namePara = new Paragraph();
        namePara.add(new Chunk("Patient Name  ", font(10, Font.NORMAL, TEXT_DARK)));
        namePara.add(new Chunk(name, font(10, Font.BOLD, TEXT_DARK)));
        // Add dotted line under the name field
        nameCell.addElement(namePara);
        nameCell.setBorderWidthBottom(0.5f);
        nameCell.setBorderColorBottom(LIGHT_GRAY);
        t.addCell(nameCell);

        PdfPCell ageCell = noBorderCell();
        Paragraph agePara = new Paragraph();
        agePara.add(new Chunk("Age  ", font(10, Font.NORMAL, TEXT_DARK)));
        agePara.add(new Chunk(age, font(10, Font.BOLD, TEXT_DARK)));
        ageCell.addElement(agePara);
        ageCell.setBorderWidthBottom(0.5f);
        ageCell.setBorderColorBottom(LIGHT_GRAY);
        t.addCell(ageCell);

        PdfPCell sexCell = noBorderCell();
        Paragraph sexPara = new Paragraph();
        sexPara.add(new Chunk("Sex : ", font(10, Font.NORMAL, TEXT_DARK)));
        sexPara.add(new Chunk(gender, font(10, Font.BOLD, TEXT_DARK)));
        sexCell.addElement(sexPara);
        sexCell.setBorderWidthBottom(0.5f);
        sexCell.setBorderColorBottom(LIGHT_GRAY);
        t.addCell(sexCell);

        PdfPCell dateCell = noBorderCell();
        Paragraph datePara = new Paragraph();
        datePara.add(new Chunk("Date  ", font(10, Font.NORMAL, TEXT_DARK)));
        datePara.add(new Chunk(date, font(10, Font.BOLD, TEXT_DARK)));
        dateCell.addElement(datePara);
        dateCell.setBorderWidthBottom(0.5f);
        dateCell.setBorderColorBottom(LIGHT_GRAY);
        t.addCell(dateCell);

        return t;
    }

    /**
     * Two-column layout: [empty body area] [vitals block on right]
     * The watermark logo fills the body area via page event.
     */
    private PdfPTable buildVitalsAndBodyTable(Prescription prescription) throws DocumentException {
        Patient p = prescription.getPatient();

        PdfPTable t = new PdfPTable(new float[]{3f, 1.4f});
        t.setWidthPercentage(100);
        t.setSpacingBefore(4f);
        t.setSpacingAfter(4f);

        // Left — patient condition (below patient name on prescription)
        PdfPCell leftCell = noBorderCell();
        leftCell.setMinimumHeight(30f);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);
        String condition = prescription.getPatientCondition();
        if (condition != null && !condition.isBlank()) {
            Paragraph condLabel = new Paragraph("Patient Condition", font(9, Font.BOLD, TEXT_DARK));
            condLabel.setSpacingAfter(3f);
            leftCell.addElement(condLabel);
            Paragraph condText = new Paragraph(condition.trim(), font(9, Font.NORMAL, TEXT_DARK));
            condText.setLeading(12f);
            leftCell.addElement(condText);
        } else {
            leftCell.addElement(new Phrase(" "));
        }
        t.addCell(leftCell);

        // Right — vitals
        PdfPCell vitalsCell = noBorderCell();
        vitalsCell.setVerticalAlignment(Element.ALIGN_TOP);

        String[][] vitals = {
                {"Temp", p != null ? nullToEmpty(p.getTemperature()) : "", "ºC / ºF"},
                {"Wt",   p != null ? nullToEmpty(p.getWeight())      : "", "kg"},
                {"Ht",   p != null ? nullToEmpty(p.getHeight())      : "", "cm"},
                {"BP",   p != null ? nullToEmpty(p.getBloodPressure()): "", "mmHg"},
                {"SpO2", p != null ? nullToEmpty(p.getSpO2())        : "", "%"},
                {"PR",   p != null ? nullToEmpty(p.getPulseRate())   : "", "/ min"},
                {"CBG",  p != null ? nullToEmpty(p.getCbg())         : "", "mg/dL"},
        };

        PdfPTable vitalsTable = new PdfPTable(new float[]{1.5f, 0.3f, 1.8f, 1.2f});
        vitalsTable.setWidthPercentage(100);

        for (String[] row : vitals) {
            // Label
            PdfPCell lbl = noBorderCell();
            lbl.addElement(new Paragraph(row[0], font(9, Font.BOLD, TEXT_DARK)));
            vitalsTable.addCell(lbl);
            // Colon
            PdfPCell colon = noBorderCell();
            colon.addElement(new Paragraph(":", font(9, Font.NORMAL, TEXT_DARK)));
            vitalsTable.addCell(colon);
            // Value
            PdfPCell val = noBorderCell();
            val.addElement(new Paragraph(row[1], font(9, Font.BOLD, RED_DEEP)));
            vitalsTable.addCell(val);
            // Unit
            PdfPCell unit = noBorderCell();
            unit.addElement(new Paragraph(row[2], font(8, Font.NORMAL, TEXT_MUTED)));
            vitalsTable.addCell(unit);
        }

        vitalsCell.addElement(vitalsTable);
        t.addCell(vitalsCell);

        return t;
    }

    /**
     * Medicine table: Rx symbol | # | Medicine | Dosage | Timing / Duration
     */
    private PdfPTable buildMedicineTable(List<PrescriptionItem> items) throws DocumentException {
        // Rx header
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingBefore(4f);

        // Rx label row
        PdfPCell rxLabelCell = noBorderCell();
        rxLabelCell.addElement(new Paragraph("Rx", font(22, Font.BOLD, RED_PRIMARY)));
        wrapper.addCell(rxLabelCell);

        // Medicine detail table
        PdfPTable table = new PdfPTable(new float[]{0.4f, 3.5f, 1.8f, 3.3f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(2f);

        // Header row
        addMedHeader(table, "#");
        addMedHeader(table, "Medicine");
        addMedHeader(table, "Dosage");
        addMedHeader(table, "Timing / Duration");

        int i = 1;
        if (items != null) {
            for (PrescriptionItem item : items) {
                boolean shaded = (i % 2 == 0);
                Color bg = shaded ? new Color(255, 245, 245) : WHITE;

                addMedCell(table, String.valueOf(i), Element.ALIGN_CENTER, bg);
                addMedCell(table, nullToEmpty(item.getMedicineName()), Element.ALIGN_LEFT, bg);
                addMedCell(table, formatDosage(item.getDosage()), Element.ALIGN_CENTER, bg);
                addMedCell(table, formatTimingColumn(item), Element.ALIGN_LEFT, bg);
                i++;
            }
        }
        if (i == 1) {
            PdfPCell empty = new PdfPCell(new Phrase("No medicines prescribed.", font(10, Font.ITALIC, TEXT_MUTED)));
            empty.setColspan(4);
            empty.setPadding(10);
            empty.setBorder(Rectangle.BOX);
            empty.setBorderColor(RED_LIGHT);
            empty.setBackgroundColor(WHITE);
            table.addCell(empty);
        }

        PdfPCell tableWrapper = noBorderCell();
        tableWrapper.addElement(table);
        wrapper.addCell(tableWrapper);

        return wrapper;
    }

    private void addMedHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(9, Font.BOLD, RED_DEEP)));
        cell.setBackgroundColor(new Color(255, 235, 238));
        cell.setBorderColor(RED_LIGHT);
        cell.setBorderWidth(1f);
        cell.setPadding(5f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addMedCell(PdfPTable table, String text, int align, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(10, Font.NORMAL, TEXT_DARK)));
        cell.setBorderColor(RED_LIGHT);
        cell.setBorderWidth(0.5f);
        cell.setPadding(5f);
        cell.setHorizontalAlignment(align);
        cell.setBackgroundColor(bg);
        table.addCell(cell);
    }

    private PdfPTable buildBillingSection(Bill bill) throws DocumentException {
        PdfPTable t = new PdfPTable(new float[]{3f, 2f});
        t.setWidthPercentage(45);
        t.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Font lbl = font(9, Font.NORMAL, TEXT_DARK);
        Font val = font(9, Font.BOLD, RED_DEEP);

        addBillRow(t, "Consultation Fee", "Rs. " + formatMoney(bill.getConsultationFee()), lbl, val);

        if (bill.getOtherCharges() != null && bill.getOtherCharges().compareTo(BigDecimal.ZERO) > 0) {
            String otherLabel = bill.getOtherChargesDescription() != null && !bill.getOtherChargesDescription().isBlank()
                    ? bill.getOtherChargesDescription().trim()
                    : "Injection / Other Charges";
            addBillRow(t, otherLabel, "Rs. " + formatMoney(bill.getOtherCharges()), lbl, val);
        }

        addBillRow(t, "Discount", "Rs. " + formatMoney(bill.getDiscount()), lbl, val);
        addBillRow(t, "Final Amount", "Rs. " + formatMoney(bill.getFinalAmount()), lbl, val);

        return t;
    }

    private void addBillRow(PdfPTable t, String label, String value, Font lbl, Font val) {
        addBillCell(t, label, lbl);
        addBillCell(t, value, val);
    }

    private void addBillCell(PdfPTable t, String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBorder(Rectangle.BOX);
        c.setBorderColor(RED_LIGHT);
        c.setBorderWidth(0.5f);
        c.setPadding(4f);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.addCell(c);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Helpers
    // ════════════════════════════════════════════════════════════════════════

    private static PdfPCell noBorderCell() {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(2f);
        return c;
    }

    private Image tryLoadImage(String webPath, float maxWidth, float maxHeight) {
        if (webPath == null || webPath.isBlank()) return null;
        try {
            Path file = resolveStaticPath(webPath);
            if (!Files.isRegularFile(file)) return null;
            Image image = Image.getInstance(file.toString());
            image.scaleToFit(maxWidth, maxHeight);
            return image;
        } catch (Exception ex) {
            log.debug("Could not load image for PDF: {}", webPath);
            return null;
        }
    }

    private Path resolveStaticPath(String webPath) {
        String relative = webPath.startsWith("/") ? webPath.substring(1) : webPath;
        return staticRoot.resolve(relative).normalize();
    }

    private static Font font(float size, int style, Color color) {
        return new Font(Font.HELVETICA, size, style, color);
    }

    private static String formatDoctorName(ClinicSettings clinic) {
        String name = clinic.getDoctorDisplayName();
        if (name == null || name.isBlank()) name = "Doctor";
        if (!name.toLowerCase(Locale.ENGLISH).startsWith("dr")) name = "Dr. " + name;
        String qual = clinic.getDoctorQualification();
        if (qual != null && !qual.isBlank()) name += " " + qual.toUpperCase(Locale.ENGLISH) + ",";
        return name;
    }

    private static String formatDosage(String dosage) {
        if (dosage == null || dosage.isBlank()) return "";
        return dosage.replace("-", " - ").trim();
    }

    private static String formatTimingColumn(PrescriptionItem item) {
        StringBuilder sb = new StringBuilder();
        if (notBlank(item.getInstructions())) sb.append(item.getInstructions());
        if (notBlank(item.getDuration())) {
            if (!sb.isEmpty()) sb.append(" - ");
            sb.append(item.getDuration());
        }
        return sb.toString();
    }

    private static String safeUpper(String v) { return v != null ? v.toUpperCase(Locale.ENGLISH) : ""; }
    private static boolean notBlank(String v)  { return v != null && !v.isBlank(); }
    private static String nullToEmpty(String v){ return v != null ? v : ""; }
    private static String formatMoney(BigDecimal v) { return v != null ? String.format("%.2f", v) : "0.00"; }
}
