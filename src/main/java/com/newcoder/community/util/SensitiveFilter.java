package com.newcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    private static String REPLACEMENT="***";
    private TrieNode root=new TrieNode();

    //初始化一次，读取sensitive-words文件，使用classlooader获得输入流，然后使用缓存字符流
    // 一行一行读取，将其添加到前缀树，方法定义为addKeyWord
    @PostConstruct
    public void init(){
        try(
                InputStream is=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword=reader.readLine())!=null){
                //逐行读取
                this.addKeyword(keyword);
            }
        }catch(IOException e){
            logger.error("fail to read sensitive word"+e.getMessage());
        }
    }

    //添加keyword到前缀树
    public void addKeyword(String key){
        TrieNode tempNode=root;
        for(int i=0;i<key.length();i++){
            char c=key.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                //子节点为空，创建新的
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //指向子节点，进入下一轮循环
            tempNode=subNode;

            //对于最后一次循环，设置结束标识
            if(i==key.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    //忽略敏感词中间的特殊字符
    //特殊字符若位于敏感词中间，连带整个敏感词***
    //否则，即指针一指在TrieNode的根节点处，保留，将此符号计入结果
    public boolean isSymbol(Character c){
        // 0x2E80-0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }


    /*三个指针
    * 1. tempNode 指向树
    * 2. begin 指向text疑似敏感词的开头
    * 3. position 指向text疑似敏感词的结尾
    * */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode=root;
        int begin=0;
        int position=0;
        StringBuilder builder=new StringBuilder();

        while(position<text.length()){
            char c=text.charAt(position);
            //检查特殊字符，单独判断
            if(isSymbol(c)){
                if(tempNode==root){
                    builder.append(c);
                    begin++;
                }
                //无论符号在开头还是结尾，position都要后移一位
                position++;
                continue;
            }

            //检查下级节点
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){
                //begin处不是敏感词的开头
                builder.append(text.charAt(begin));
                position=++begin;
                tempNode=root;
            }else if(tempNode.isKeywordEnd()){
                //begin-position是敏感词
                builder.append(REPLACEMENT);
                //寻找下一个敏感词
                begin=++position;
                tempNode=root;
            }else{
                //检查下一个字符
                position++;
            }

        }//position指向末尾的下一个字符，为空时，结束while循环

        // 将最后一批字符计入结果
        builder.append(text.substring(begin));
        return builder.toString();
    }


    public class TrieNode{
        //关键词结束标识
        private boolean isKeywordEnd=false;

        //子节点(key是下级字符，value是下级节点）
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            this.isKeywordEnd=keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode value){
            subNodes.put(c,value);
        }

        //获取子节点
        public TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }
    }
}
