package jp.co.monolithworks.il.iris;

import java.util.Map;
import java.util.HashMap;

public class ConsumeLimit{

    public static Map<String,String> limit;

    public ConsumeLimit(){
        limit = new HashMap<String,String>();

        setLimitday();
    }

    private void setLimitday(){

        limit.put("トマト","１４");
        limit.put("キュウリ","２");
        limit.put("タマネギ","１４");
        limit.put("ナス","２");
        limit.put("ニンジン","５");//冬なら1週間としか書いてありませんでした。
        limit.put("ジャガイモ","３０");
        limit.put("ダイコン","１０");
        limit.put("キャベツ","１４");
        limit.put("レタス","４");
        limit.put("ホウレンソウ","７");
        limit.put("ピーマン","７");
        limit.put("バナナ","７");
        limit.put("リンゴ","３０");
        limit.put("イチゴ","４");
        limit.put("ブドウ","１４");
        limit.put("キウイフルーツ","７");//1~2週間
        limit.put("シイタケ","６");
        limit.put("生クリーム","９０");
        limit.put("マーガリン","１８０");
        limit.put("ヨーグルト","１４");//プルーン
        limit.put("バター","１８０");
        limit.put("チーズ","４");//開封後
        limit.put("牛乳","７");
        limit.put("パックジュース","１５");
        limit.put("ビール","２７０");
        limit.put("冷凍食品","３６５");
        limit.put("マヨネーズ","９０");
        limit.put("ケチャップ","５５０");
        limit.put("肉","２");
        limit.put("魚","４");
    }
}