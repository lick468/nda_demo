package com.nenu.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.nenu.domain.TblNdashare;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.*;

public class PDFUtils {

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
           // System.out.println("===>pdf : " + pdfFile.getName() +" , size : " + size);
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
            /// 关闭reader
            reader = null;
            //reader.close();
            document.close();
            buffer.append("</body>\r\n");
            buffer.append("</html>");
            end = System.currentTimeMillis() - start;
            //System.out.println("===> Reading pdf times: " + (end/1000));
            start = end = null;
            //生成网页文件
            fos = new FileOutputStream(path+randStr+".html");
           // System.out.println(path+randStr+".html");
            fos.write(buffer.toString().getBytes());
            fos.flush(); fos.close();
            buffer.setLength(0);

        }catch(Exception e){
            System.out.println("===>Reader parse pdf to jpg error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean createNdaItemFile(@NotNull String pathNDA, @NotNull TblNdashare tblNdashare,
                                            @NotNull String ndaItems){
        String ndaTitle = tblNdashare.getNdatitle();
        String creatorName = tblNdashare.getCreateusername();
        String receiverName = tblNdashare.getUsername();
        return createNdaItemFile(pathNDA, ndaTitle, creatorName, receiverName, ndaItems);
    }
    
    public static boolean createNdaItemFile(@NotEmpty String pathNDA, @NotNull String ndaTitle,
                                            @NotNull String creatorName, @NotNull String ReceiverName,
                                            @NotNull String ndaItems){
        //生成NDA条款.pdf文件  上传到IPFS
        //页面大小
        StringReader title = null;
        StringReader sender = null;
        StringReader receiver = null;
        HTMLWorker htmlWorker = null;
        Document pdfDoc = null;
        PdfWriter pdfWriter = null;
        try {
            Rectangle rect = new Rectangle(PageSize.A4.rotate());
            //页面背景色
            rect.setBackgroundColor(BaseColor.WHITE);
            pdfDoc = new Document(rect);
            myFileUtils.CreateFilewithDir(pathNDA);
            pdfWriter = PdfWriter.getInstance(pdfDoc, new FileOutputStream(pathNDA));
            //页边空白
            pdfDoc.setMargins(10, 20, 30, 40);
            pdfDoc.open();
            //Step 4—Add content.
    
            title = new StringReader("<h1  style=\"text-align: center\">"+ ndaTitle +"</h1>");
            sender = new StringReader("<h4  style=\"text-align: left\"> 发起人："+ creatorName + "</h4>");
            receiver = new StringReader("<h4  style=\"text-align: left\"> 接收人："+ ReceiverName +"</h4>");
            htmlWorker = new HTMLWorker(pdfDoc);
            htmlWorker.parse(title);
            htmlWorker.parse(sender);
            htmlWorker.parse(receiver);
            htmlWorker.parse(new StringReader(ndaItems));
            pdfDoc.close();
            return true;
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            title = null;
            sender = null;
            receiver = null;
            htmlWorker = null;
            pdfWriter = null;
            pdfDoc = null;
        }
    }
}
