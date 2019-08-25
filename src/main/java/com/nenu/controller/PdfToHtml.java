package com.nenu.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class PdfToHtml {
//    public static void main(String[] args) {
//        //传入PDF地址
//        PdfToImage("E:\\upload\\test.pdf","E:\\upload\\1");
//
//
//    }


    public static void PdfToImage(String pdfurl,String outPath){
        StringBuffer buffer = new StringBuffer();
        FileOutputStream fos;
        PDDocument document;
        File pdfFile;
        int size;
        BufferedImage image;
        FileOutputStream out;
        Long randStr = 0l;
        //PDF转换成HTML保存的文件夹
        String path = outPath;
        File htmlsDir = new File(path);
        if(!htmlsDir.exists()){
            htmlsDir.mkdirs();
        }
        File htmlDir = new File(path+"/");
        if(!htmlDir.exists()){
            htmlDir.mkdirs();
        }
        try{
            //遍历处理pdf附件
            randStr = System.currentTimeMillis();
            buffer.append("<!doctype html>\r\n");
            buffer.append("<head>\r\n");
            buffer.append("<meta charset=\"UTF-8\">\r\n");
            buffer.append("</head>\r\n");
            buffer.append("<body style=\"background-color:gray;\">\r\n");
            buffer.append("<style>\r\n");
            buffer.append("img {background-color:#fff; text-align:center; width:100%; max-width:100%;margin-top:6px;}\r\n");
            buffer.append("</style>\r\n");
            document = new PDDocument();
            //pdf附件
            pdfFile = new File(pdfurl);
            document = PDDocument.load(pdfFile, (String) null);
            size = document.getNumberOfPages();
            Long start = System.currentTimeMillis(), end = null;
            System.out.println("===>pdf : " + pdfFile.getName() +" , size : " + size);
            PDFRenderer reader = new PDFRenderer(document);
            for(int i=0 ; i < size; i++){
                //image = new PDFRenderer(document).renderImageWithDPI(i,130,ImageType.RGB);
                image = reader.renderImage(i, 1.5f);
                //生成图片,保存位置
                out = new FileOutputStream(path + "/"+ "image" + "_" + i + ".jpg");
                ImageIO.write(image, "png", out); //使用png的清晰度
                //将图片路径追加到网页文件里
                buffer.append("<img src=\"" + path +"/"+ "image" + "_" + i + ".jpg\"/>\r\n");
                image = null; out.flush(); out.close();
            }
            reader = null;
            document.close();
            buffer.append("</body>\r\n");
            buffer.append("</html>");
            end = System.currentTimeMillis() - start;
            System.out.println("===> Reading pdf times: " + (end/1000));
            start = end = null;
            //生成网页文件
            fos = new FileOutputStream(path+randStr+".html");
            System.out.println(path+randStr+".html");
            fos.write(buffer.toString().getBytes());
            fos.flush(); fos.close();
            buffer.setLength(0);

        }catch(Exception e){
            System.out.println("===>Reader parse pdf to jpg error : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
