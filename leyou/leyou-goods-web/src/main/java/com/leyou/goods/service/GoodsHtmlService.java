package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author: Alaska He
 * Date: 2018/11/2 0002
 * Time: 14:29
 */
@Service
public class GoodsHtmlService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;

    public void getHtml(Long spuId) {
        //初始化上下文
        Context context = new Context();
        //获取数据模型
        Map<String, Object> map = this.goodsService.loadDate(spuId);
        //将数据模型放入上下文对象
        context.setVariables(map);

        //获取输出流
        PrintWriter writer = null;
        try {
            //出了异常自己解决，不能影响页面的查询
            File file = new File("E:\\nginx-1.14.0\\html\\item\\");
            writer = new PrintWriter(file);
            this.templateEngine.process("item", context, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }


    }
}
