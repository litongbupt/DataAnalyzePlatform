package com.bupt.app.multivrPC.dao;

import java.util.List;

import com.bupt.app.multivrPC.model.WordPC;
import com.bupt.app.multivrPC.model.WordPCExample;

public interface WordPCMapper {

    int countByExample(WordPCExample example);

    List<WordPC> selectByExample(WordPCExample example);
    
    int countDayByExample(WordPCExample example);

    List<WordPC> selectDayByExample(WordPCExample example);

}