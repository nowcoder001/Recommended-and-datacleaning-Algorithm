import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.example.GroupLensDataModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecommendedUtil {

    public static final String ModelFileName = "src/main/resources/ml-25m/ratings2.csv";//模型数据的路径，需要提前从数据库中抽取出来


    /**
     *  基于用户的推荐
     *  提前准备csv文件 格式为  userID，tagID，count（用户看此tag的博客多少次）
     *  输入csv文件的 路径  输出推荐的结果
     * @param user_id  为id为user_id的用户推荐size个标签
     * @param size
     * @return  输出为 RecommendedItem[item:3198, value:5.0] 输出为tag_id
     * @throws IOException
     * @throws TasteException
     */
    public static List<RecommendedItem> UserBasedR(int user_id, int size) throws IOException, TasteException {

        DataModel dataModel = new FileDataModel(new File(ModelFileName));//构造数据模型

        //计算相似度，相似度算法有很多种，欧几里得、皮尔逊等等。这里基于皮尔逊
        UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);

        //计算最近邻域，邻居有两种算法，基于固定数量的邻居和基于相似度的邻居，这里使用基于固定数量的邻居 选择用户数量为
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
        //构建推荐器，协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于用户的协同过滤推荐
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        //给用户ID等于5的用户推荐10部电影
        List<RecommendedItem> recommendedItemList = recommender.recommend(user_id, size);
        return recommendedItemList;
    }


    /***
     * 基于标签的推荐   用户当前正在浏览的博客  有几个标签类别  根据用户浏览的博客的标签 推荐相似的标签tag
     *         提前准备csv文件 格式为  userID，tagID，count（用户看此tag的博客多少次）
     *         输入csv文件的 路径  输出推荐的结果
     * @param user_id   为id为user_id的用户推荐size个标签 基于用户正在浏览的博客的标签tag_id
     * @param tag_id
     * @param size
     * @return  输出为 RecommendedItem[item:3198, value:5.0] 输出为tag_id
     * @throws IOException
     * @throws TasteException
     */
    public static List<RecommendedItem> ItemBaseRTag(int user_id,int tag_id,int size) throws IOException, TasteException {

        DataModel dataModel = new FileDataModel(new File(ModelFileName));//构造数据模型
        //计算相似度，相似度算法有很多种，欧几里得、皮尔逊等等。
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        //构建推荐器，协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于物品的协同过滤推荐
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        //给用户ID等于user_id的用户推荐size个与标签tag_id类似的标签
        List<RecommendedItem> recommendedItemList = recommender.recommendedBecause(user_id, tag_id, size);
        return recommendedItemList;

    }

    /***
     *   基于标签的推荐
     *     提前准备csv文件 格式为  userID，tagID，count（用户看此tag的博客多少次）
     *    输入csv文件的 路径  输出推荐的结果
     * @param user_id
     * @param size
     * @return    为id为user_id的用户推荐size个标签
     * @throws IOException
     * @throws TasteException
     */
    public static List<RecommendedItem> ItemBaseR(int user_id,int size) throws IOException, TasteException {

        DataModel dataModel = new FileDataModel(new File(ModelFileName));//构造数据模型
        //计算相似度，相似度算法有很多种，欧几里得、皮尔逊等等。
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
        //构建推荐器，协同过滤推荐有两种，分别是基于用户的和基于物品的，这里使用基于物品的协同过滤推荐
        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
        //给用户ID等于user_id的用户推荐size个标签
        List<RecommendedItem> recommendedItemList = recommender.recommend(user_id, size);
        return recommendedItemList;

    }
    public  static  void Evaluator() throws IOException, TasteException {
        //准备数据 这里是电影评分数据
        DataModel dataModel = new FileDataModel(new File(ModelFileName));//构造数据模型
        //推荐评估，使用均方根
        //RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
        //推荐评估，使用平均差值
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder builder = new RecommenderBuilder() {

            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
                return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            }
        };
        // 用70%的数据用作训练，剩下的30%用来测试
        double score = evaluator.evaluate(builder, null, dataModel, 0.7, 1.0);
        //最后得出的评估值越小，说明推荐结果越好
        System.out.println(score);
    }

    public static  void IRStatistics() throws IOException, TasteException {
        //准备数据 这里是电影评分数据
        DataModel dataModel = new FileDataModel(new File(ModelFileName));//构造数据模型
        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();
        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(4, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };
        // 计算推荐4个结果时的查准率和召回率
        //使用评估器，并设定评估期的参数
        //4表示"precision and recall at 4"即相当于推荐top4，然后在top-4的推荐上计算准确率和召回率
        IRStatistics stats = statsEvaluator.evaluate(recommenderBuilder, null, dataModel, null, 4, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
        System.out.println(stats.getPrecision());
        System.out.println(stats.getRecall());
    }
    public static void main(String[] args) throws Exception {
        List<RecommendedItem> list1 = UserBasedR(166,10);
        System.out.println("使用基于用户的协同过滤算法");
        System.out.println("为用户166推荐10个商品");
        for (RecommendedItem recommendedItem : list1) {
            System.out.println(recommendedItem);
        }


        List<RecommendedItem> list2 = ItemBaseR(166,12);
        System.out.println("使用基于用户的协同过滤算法");
        System.out.println("为用户5推荐10个商品");
        for (RecommendedItem recommendedItem : list2) {
            System.out.println(recommendedItem);
        }

        List<RecommendedItem> list3 = ItemBaseRTag(5,34,10);
        System.out.println("使用基于用户的协同过滤算法");
        System.out.println("为用户5推荐10个商品");
        for (RecommendedItem recommendedItem : list3) {
            System.out.println(recommendedItem);
        }
    }
}
