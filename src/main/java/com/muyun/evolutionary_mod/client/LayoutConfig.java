package com.muyun.evolutionary_mod.client;

import com.muyun.evolutionary_mod.core.AccessorySlot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LayoutConfig {
    public static class Layout {
        public int cellSize = 40;
        public int gap = 10;
        public int pageWidth = 250;
        public int pageHeight = 200;
        public boolean showGrid = true;
        public int gridStep = 10;
        public String background = null;
        public String title = "Accessories";
        public int inventoryStartX = 8;
        public int inventoryStartY = 86;
        public final Map<AccessorySlot, int[]> slots = new EnumMap<>(AccessorySlot.class);
    }

    private static final Pattern SLOT_PATTERN = Pattern.compile("\"([A-Z0-9_]+)\"\\s*:\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]");

    public static Layout load(Path path) {
        Layout layout = defaultLayout();
        if (Files.exists(path)) {
            try {
                String txt = Files.readString(path, StandardCharsets.UTF_8);
                // parse cellSize
                Matcher m = Pattern.compile("\"cellSize\"\\s*:\\s*(\\d+)").matcher(txt);
                if (m.find()) layout.cellSize = Integer.parseInt(m.group(1));
                m = Pattern.compile("\"gap\"\\s*:\\s*(\\d+)").matcher(txt);
                if (m.find()) layout.gap = Integer.parseInt(m.group(1));
                m = Pattern.compile("\"pageWidth\"\\s*:\\s*(\\d+)").matcher(txt);
                if (m.find()) layout.pageWidth = Integer.parseInt(m.group(1));
                m = Pattern.compile("\"pageHeight\"\\s*:\\s*(\\d+)").matcher(txt);
                if (m.find()) layout.pageHeight = Integer.parseInt(m.group(1));
                m = Pattern.compile("\"showGrid\"\\s*:\\s*(true|false)").matcher(txt);
                if (m.find()) layout.showGrid = Boolean.parseBoolean(m.group(1));
                m = Pattern.compile("\"gridStep\"\\s*:\\s*(\\d+)").matcher(txt);
                if (m.find()) layout.gridStep = Integer.parseInt(m.group(1));
                m = Pattern.compile("\"background\"\\s*:\\s*\"([^\"]+)\"").matcher(txt);
                if (m.find()) layout.background = m.group(1);
                m = Pattern.compile("\"title\"\\s*:\\s*\"([^\"]+)\"").matcher(txt);
                if (m.find()) layout.title = m.group(1);
                m = Pattern.compile("\"inventoryStartX\"\\s*:\\s*(\\d+)").matcher(txt);
                if (m.find()) layout.inventoryStartX = Integer.parseInt(m.group(1));
                m = Pattern.compile("\"inventoryStartY\"\\s*:\\s*(\\d+)").matcher(txt);
                if (m.find()) layout.inventoryStartY = Integer.parseInt(m.group(1));

                Matcher sm = SLOT_PATTERN.matcher(txt);
                while (sm.find()) {
                    String name = sm.group(1);
                    int x = Integer.parseInt(sm.group(2));
                    int y = Integer.parseInt(sm.group(3));
                    try {
                        AccessorySlot slot = AccessorySlot.valueOf(name);
                        layout.slots.put(slot, new int[]{x, y});
                    } catch (IllegalArgumentException ignored) {}
                }
            } catch (IOException ignored) {}
        }
        return layout;
    }

    public static Layout loadFromString(String txt) {
        Layout layout = defaultLayout();
        if (txt == null || txt.isEmpty()) return layout;
        Matcher m = Pattern.compile("\"cellSize\"\\s*:\\s*(\\d+)").matcher(txt);
        if (m.find()) layout.cellSize = Integer.parseInt(m.group(1));
        m = Pattern.compile("\"gap\"\\s*:\\s*(\\d+)").matcher(txt);
        if (m.find()) layout.gap = Integer.parseInt(m.group(1));
        m = Pattern.compile("\"pageWidth\"\\s*:\\s*(\\d+)").matcher(txt);
        if (m.find()) layout.pageWidth = Integer.parseInt(m.group(1));
        m = Pattern.compile("\"pageHeight\"\\s*:\\s*(\\d+)").matcher(txt);
        if (m.find()) layout.pageHeight = Integer.parseInt(m.group(1));
        m = Pattern.compile("\"showGrid\"\\s*:\\s*(true|false)").matcher(txt);
        if (m.find()) layout.showGrid = Boolean.parseBoolean(m.group(1));
        m = Pattern.compile("\"gridStep\"\\s*:\\s*(\\d+)").matcher(txt);
        if (m.find()) layout.gridStep = Integer.parseInt(m.group(1));
        m = Pattern.compile("\"background\"\\s*:\\s*\"([^\"]+)\"").matcher(txt);
        if (m.find()) layout.background = m.group(1);
        m = Pattern.compile("\"title\"\\s*:\\s*\"([^\"]+)\"").matcher(txt);
        if (m.find()) layout.title = m.group(1);
        m = Pattern.compile("\"inventoryStartX\"\\s*:\\s*(\\d+)").matcher(txt);
        if (m.find()) layout.inventoryStartX = Integer.parseInt(m.group(1));
        m = Pattern.compile("\"inventoryStartY\"\\s*:\\s*(\\d+)").matcher(txt);
        if (m.find()) layout.inventoryStartY = Integer.parseInt(m.group(1));

        Matcher sm = SLOT_PATTERN.matcher(txt);
        while (sm.find()) {
            String name = sm.group(1);
            int x = Integer.parseInt(sm.group(2));
            int y = Integer.parseInt(sm.group(3));
            try {
                AccessorySlot slot = AccessorySlot.valueOf(name);
                layout.slots.put(slot, new int[]{x, y});
            } catch (IllegalArgumentException ignored) {}
        }
        return layout;
    }

    public static void save(Path path, Layout layout) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"cellSize\": ").append(layout.cellSize).append(",\n");
        sb.append("  \"gap\": ").append(layout.gap).append(",\n");
        sb.append("  \"pageWidth\": ").append(layout.pageWidth).append(",\n");
        sb.append("  \"pageHeight\": ").append(layout.pageHeight).append(",\n");
        sb.append("  \"showGrid\": ").append(layout.showGrid).append(",\n");
        sb.append("  \"gridStep\": ").append(layout.gridStep).append(",\n");
        sb.append("  \"background\": \"").append(layout.background == null ? "" : layout.background).append("\",\n");
        sb.append("  \"title\": \"").append(layout.title == null ? "" : layout.title).append("\",\n");
        sb.append("  \"slots\": {\n");
        boolean first = true;
        for (AccessorySlot slot : AccessorySlot.values()) {
            int[] p = layout.slots.getOrDefault(slot, new int[]{0,0});
            if (!first) sb.append(",\n");
            sb.append("    \"").append(slot.name()).append("\": [").append(p[0]).append(", ").append(p[1]).append("]");
            first = false;
        }
        sb.append("\n  },\n");
        sb.append("  \"inventoryStartX\": ").append(layout.inventoryStartX).append(",\n");
        sb.append("  \"inventoryStartY\": ").append(layout.inventoryStartY).append("\n");
        sb.append("}");
        Files.createDirectories(path.getParent());
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    public static Layout defaultLayout() {
        Layout layout = new Layout();
        // default positions - compact two-row layout
        int[][] coords = {
                {32,30},{56,30},{80,30},{104,30},
                {128,30},{152,30},{176,30},{200,30},
                {32,54},{56,54},{80,54},{104,54},
                {128,54},{152,54},{176,54}
        };
        int i = 0;
        for (AccessorySlot slot : AccessorySlot.values()) {
            if (i < coords.length) layout.slots.put(slot, new int[]{coords[i][0], coords[i][1]});
            else layout.slots.put(slot, new int[]{30,155});
            i++;
        }
        layout.cellSize = 18;
        layout.gap = 6;
        layout.pageWidth = 178;
        layout.pageHeight = 160;
        layout.inventoryStartX = 8;
        layout.inventoryStartY = 86;
        layout.showGrid = true;
        layout.gridStep = 10;
        return layout;
    }
}


