

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;

import java.io.File;
import java.util.List;

/**
 *
 */
public class ItemBasedCF {
    public static void main(String[] args) throws Exception {
        DataModel dataModel = new FileDataModel(new File("src/main/resources/ml-25m/ratings2.csv"));//构造数据模型
        //计算相似度，相似度算法有很多种，欧几里得、皮尔逊等等。
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        //构建推荐器，协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于物品的协同过滤推荐
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        //给用户ID等于5的用户推荐10个与2398相似的商品
        List<RecommendedItem> recommendedItemList = recommender.recommendedBecause(5, 2398, 10);
        //打印推荐的结果
        System.out.println("使用基于物品的协同过滤算法");
        System.out.println("根据用户5当前浏览的商品2398，推荐10个相似的商品");
        for (RecommendedItem recommendedItem : recommendedItemList) {
            System.out.println(recommendedItem);
        }
        long start = System.currentTimeMillis();
        recommendedItemList = recommender.recommendedBecause(5, 34, 10);
        //打印推荐的结果
        System.out.println("使用基于物品的协同过滤算法");
        System.out.println("根据用户5当前浏览的商品34，推荐10个相似的商品");
        for (RecommendedItem recommendedItem : recommendedItemList) {
            System.out.println(recommendedItem);
        }
        System.out.println(System.currentTimeMillis() -start);
    }
}