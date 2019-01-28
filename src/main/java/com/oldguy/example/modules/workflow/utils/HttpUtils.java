package com.oldguy.example.modules.workflow.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author huangrenhao
 * @date 2019/1/22
 */
public class HttpUtils {

    public static void copyImageStream(InputStream inputStream,OutputStream outputStream){
        try {
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
