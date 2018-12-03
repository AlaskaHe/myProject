package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author: Alaska He
 * Date: 2018/11/1 0001
 * Time: 20:10
 */
@Controller
@RequestMapping("/item")
public class GoodsController {
    @Autowired
    private GoodsHtmlService goodsHtmlService;
    @Autowired
    private GoodsService goodsService;
    @GetMapping("/{spuId}.html")
    public String toItemPage(Model model, @PathVariable("spuId")Long spuId){
           Map<String,Object> modleMap =  this.goodsService.loadDate(spuId);
           model.addAttribute(modleMap);
           //goodsHtmlService.getHtml(spuId);
        return "item";
    }
}
