package jp.co.monolithworks.il.iris;

import java.util.Map;
import java.util.HashMap;

public class ConsumeLimit{

    public static String[][] limit;
    
    public static final String MEET = "1";
    public static final String FISH = "2";
    public static final String VEGETABLE = "3";
    public static final String DRINK = "4";
    public static final String DAIRY_PRODUCTS = "5";
    public static final String FRUIT = "6";
    public static final String PROCESSED_FOOD = "7";
    public static final String CONDIMENT = "8";
    public static final String FROZEN_FOOD = "9";

    private String[][] mLimit = new String[][] {
             {"トマト","１４",VEGETABLE}
            ,{"キュウリ","２",VEGETABLE}
            ,{"タマネギ","１４",VEGETABLE}
            ,{"ナス","２",VEGETABLE}
            ,{"ニンジン","５",VEGETABLE}//冬なら1週間としか書いてありませんでした。
            ,{"ジャガイモ","３０",VEGETABLE}
            ,{"ダイコン","１０",VEGETABLE}
            ,{"キャベツ","１４",VEGETABLE}
            ,{"レタス","４",VEGETABLE}
            ,{"ホウレンソウ","７",VEGETABLE}
            ,{"ピーマン","７",VEGETABLE}
            ,{"バナナ","７",FRUIT}
            ,{"リンゴ","３０",FRUIT}
            ,{"イチゴ","４",FRUIT}
            ,{"ブドウ","１４",FRUIT}
            ,{"キウイフルーツ","７",FRUIT}//1~2週間
            ,{"シイタケ","６",VEGETABLE}
            ,{"生クリーム","９０",PROCESSED_FOOD}
            ,{"マーガリン","１８０",PROCESSED_FOOD}
            ,{"ヨーグルト","１４",PROCESSED_FOOD}//プルーン
            ,{"バター","１８０",PROCESSED_FOOD}
            ,{"チーズ","４",PROCESSED_FOOD}//開封後
            ,{"牛乳","７",DRINK}
            ,{"パックジュース","１５",DRINK}
            ,{"ビール","２７０",DRINK}
            ,{"冷凍食品","３６５",FROZEN_FOOD}
            ,{"マヨネーズ","９０",CONDIMENT}
            ,{"ケチャップ","５５０",CONDIMENT}
            ,{"肉","２",MEET}
            ,{"魚","４",FISH}
    };
    
    public ConsumeLimit(){
        setLimitday();
    }
    
    public ConsumeLimit(int category){
        setLimitday(category);
    }

    private void setLimitday(){
        limit = mLimit;
    }
    
    public int getCategoryLength(String[][] limit, int category){
        int limitLength = 0;
        for (String[] element : limit){
            if(Integer.parseInt(element[2]) == category){
                limitLength++;
            }
        }
        return limitLength;
    }
    
    private void setLimitday(int category){
        int length = getCategoryLength(mLimit,category);
        String[][] limitday = new String[length][];
        if (category == Integer.parseInt(MEET)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(MEET)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(FISH)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(FISH)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(VEGETABLE)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(VEGETABLE)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(DRINK)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(DRINK)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(DAIRY_PRODUCTS)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(DAIRY_PRODUCTS)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(FRUIT)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(FRUIT)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(PROCESSED_FOOD)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(PROCESSED_FOOD)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(CONDIMENT)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(CONDIMENT)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }else if (category == Integer.parseInt(FROZEN_FOOD)){
            int count = 0;
            for (String[] categoryLimit : mLimit){
                if(categoryLimit[2].equals(FROZEN_FOOD)){
                    limitday[count] = categoryLimit;
                    count++;
                }
            }
        }
        limit = limitday;
    }
}
