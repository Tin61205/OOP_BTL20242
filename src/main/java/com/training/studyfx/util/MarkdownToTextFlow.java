package com.training.studyfx.util;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownToTextFlow {
    
    public static TextFlow parseMarkdown(String markdown) {
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(2);
        
        // Tách văn bản thành các đoạn
        String[] paragraphs = markdown.split("\n\n");
        
        for (int i = 0; i < paragraphs.length; i++) {
            String paragraph = paragraphs[i].trim();
            if (!paragraph.isEmpty()) {
                processMarkdownParagraph(paragraph, textFlow);
                
                // Thêm khoảng cách giữa các đoạn (trừ đoạn cuối)
                if (i < paragraphs.length - 1) {
                    Text lineBreak = new Text("\n\n");
                    textFlow.getChildren().add(lineBreak);
                }
            }
        }
        
        return textFlow;
    }
    
    private static void processMarkdownParagraph(String paragraph, TextFlow textFlow) {
        // Xử lý danh sách bullet points
        if (paragraph.contains("*") && paragraph.contains("\n")) {
            processListItems(paragraph, textFlow);
            return;
        }
        
        // Xử lý heading
        if (paragraph.startsWith("#")) {
            processHeading(paragraph, textFlow);
            return;
        }
        
        // Xử lý đoạn văn thông thường
        processInlineFormatting(paragraph, textFlow);
    }
    
    private static void processListItems(String listText, TextFlow textFlow) {
        String[] lines = listText.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("*") && line.length() > 1) {
                // Tạo bullet point
                Text bullet = new Text("• ");
                bullet.setFill(Color.web("#2e7d32"));
                bullet.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                textFlow.getChildren().add(bullet);
                
                // Xử lý nội dung sau bullet point
                String content = line.substring(1).trim();
                processInlineFormatting(content, textFlow);
                
                // Thêm xuống dòng
                Text lineBreak = new Text("\n");
                textFlow.getChildren().add(lineBreak);
            } else if (!line.isEmpty()) {
                processInlineFormatting(line, textFlow);
                Text lineBreak = new Text("\n");
                textFlow.getChildren().add(lineBreak);
            }
        }
    }
    
    private static void processHeading(String heading, TextFlow textFlow) {
        // Đếm số # để xác định level
        int level = 0;
        while (level < heading.length() && heading.charAt(level) == '#') {
            level++;
        }
        
        String content = heading.substring(level).trim();
        Text headingText = new Text(content + "\n");
        
        // Định dạng theo level
        double fontSize = Math.max(14, 20 - level * 2);
        headingText.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
        headingText.setFill(Color.web("#1565c0"));
        
        textFlow.getChildren().add(headingText);
    }
    
    private static void processInlineFormatting(String text, TextFlow textFlow) {
        List<TextSegment> segments = parseInlineText(text);
        
        for (TextSegment segment : segments) {
            Text textNode = new Text(segment.content);
            
            // Áp dụng formatting
            Font font;
            if (segment.isBold && segment.isItalic) {
                font = Font.font("Arial", FontWeight.BOLD, 14);
                textNode.setStyle("-fx-font-style: italic;");
            } else if (segment.isBold) {
                font = Font.font("Arial", FontWeight.BOLD, 14);
            } else if (segment.isItalic) {
                font = Font.font("Arial", 14);
                textNode.setStyle("-fx-font-style: italic;");
            } else if (segment.isCode) {
                font = Font.font("Consolas", 13);
                textNode.setFill(Color.web("#d32f2f"));
                textNode.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 3px;");
            } else {
                font = Font.font("Arial", 14);
            }
            
            textNode.setFont(font);
            textNode.setFill(segment.isCode ? Color.web("#d32f2f") : Color.web("#2e7d32"));
            
            textFlow.getChildren().add(textNode);
        }
    }
    
    private static List<TextSegment> parseInlineText(String text) {
        List<TextSegment> segments = new ArrayList<>();
        
        // Pattern để tìm bold (**text**), italic (*text*), code (`text`)
        Pattern pattern = Pattern.compile("(\\*\\*(.+?)\\*\\*)|(\\*(.+?)\\*)|(\\`(.+?)\\`)");
        Matcher matcher = pattern.matcher(text);
        
        int lastEnd = 0;
        
        while (matcher.find()) {
            // Thêm text trước match
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                segments.add(new TextSegment(beforeText, false, false, false));
            }
            
            // Xử lý match
            if (matcher.group(1) != null) {
                // Bold text
                segments.add(new TextSegment(matcher.group(2), true, false, false));
            } else if (matcher.group(3) != null) {
                // Italic text
                segments.add(new TextSegment(matcher.group(4), false, true, false));
            } else if (matcher.group(5) != null) {
                // Code text
                segments.add(new TextSegment(matcher.group(6), false, false, true));
            }
            
            lastEnd = matcher.end();
        }
        
        // Thêm text còn lại
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            segments.add(new TextSegment(remainingText, false, false, false));
        }
        
        return segments;
    }
    
    private static class TextSegment {
        String content;
        boolean isBold;
        boolean isItalic;
        boolean isCode;
        
        TextSegment(String content, boolean isBold, boolean isItalic, boolean isCode) {
            this.content = content;
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.isCode = isCode;
        }
    }
} 