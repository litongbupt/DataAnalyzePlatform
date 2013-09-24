package com.bupt.app.multivrWAP.dao;

import java.util.List;

import com.bupt.app.multivrWAP.model.WordWAP;
import com.bupt.app.multivrWAP.model.WordWAPExample;

public interface WordWAPMapper {


    int countByExample(WordWAPExample example);

    List<WordWAP> selectByExample(WordWAPExample example);
    
    int countDayByExample(WordWAPExample example);

    List<WordWAP> selectDayByExample(WordWAPExample example);
}