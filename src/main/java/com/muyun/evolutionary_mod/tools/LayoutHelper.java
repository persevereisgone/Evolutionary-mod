package com.muyun.evolutionary_mod.tools;

import com.muyun.evolutionary_mod.core.AccessorySlot;
import com.muyun.evolutionary_mod.client.LayoutConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * 帮助工具：生成带网格和坐标的布局模板PNG
 * 运行方式：java -cp build/classes/java/main com.muyun.evolutionary_mod.tools.LayoutHelper
 */
public class LayoutHelper {
    public static void main(String[] args) {
        try {
            // 加载当前布局
            LayoutConfig.Layout layout = LayoutConfig.defaultLayout();

            // 尝试从config加载
            try {
                layout = LayoutConfig.load(Paths.get("config", "accessories_layout.json"));
            } catch (Exception e) {
                System.out.println("使用默认布局");
            }

            generateTemplate(layout);
            Logger.getLogger("模板已生成: layout_template.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateTemplate(LayoutConfig.Layout layout) throws Exception {
        int width = layout.pageWidth;
        int height = layout.pageHeight;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 背景
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, width, height);

        // 绘制网格
        g.setColor(Color.GRAY);
        for (int x = 0; x <= width; x += layout.gridStep) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y <= height; y += layout.gridStep) {
            g.drawLine(0, y, width, y);
        }

        // 绘制坐标标签
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 8));
        FontMetrics fm = g.getFontMetrics();

        for (int x = 0; x <= width; x += layout.gridStep) {
            String label = String.valueOf(x);
            int textWidth = fm.stringWidth(label);
            g.drawString(label, x - textWidth/2, 10);
        }
        for (int y = 0; y <= height; y += layout.gridStep) {
            String label = String.valueOf(y);
            g.drawString(label, 2, y + fm.getAscent()/2);
        }

        // 绘制饰品栏位置
        g.setColor(Color.YELLOW);
        g.setStroke(new BasicStroke(2));

        for (AccessorySlot slot : AccessorySlot.values()) {
            int[] pos = layout.slots.get(slot);
            if (pos != null) {
                g.drawRect(pos[0], pos[1], layout.cellSize, layout.cellSize);

                // 绘制槽位名称
                g.setColor(Color.CYAN);
                g.drawString(slot.name(), pos[0] + 2, pos[1] + layout.cellSize - 2);
                g.setColor(Color.YELLOW);
            }
        }

        g.dispose();

        // 保存图片
        File output = new File("layout_template.png");
        ImageIO.write(image, "PNG", output);
    }
}
