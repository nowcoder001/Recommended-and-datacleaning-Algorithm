package CF;

import java.util.*;

import util.Csv.*;

import javax.swing.text.html.parser.Entity;

import static util.Csv.readCsvFile;

/**
 *
 *基于用户对标签的偏好 计算一个标签相似的标签
 */
public class ItemBasedCF {


    // 找到相似的n_sim_movie部电影，为目标用户推荐n_rec_movie部电影
    private int  n_sim_tag  ;
    private int  n_rec_tag    ;
    private  Map<String,ArrayList<TagRating>> data_set;// 存储数据集   每一个Map的key为用户id  value为列表  对应存储博客标签以及该用户对此标签的点击率点击率  用户ID	tag ID集合

    private  Map<String,Double> tag_popular;// 对应每个tag的点击率只和  key为tag_id value为点击率只和
    private  int tag_count;//总共有多少个标签

    private Map<String,Integer>  user_map; //辅助存储每一个用户的用户ID映射

    private Map<String,Integer>  tag_map; //辅助存储每一个tag的tag_id映射
    private  String  invert_tag_map [];//存储 tag_map的逆向映射


//    private  double tag_sim_matrix [][];//标签相似矩阵  计算了同时喜欢两个标签的用户数量key为一个标签  value为另一个标签以及两个标签公共被喜欢的次数
    private  Map<String,Map<String,Double>> tag_sim_matrix ;//标签相似矩阵  计算了同时喜欢两个标签的用户数量key为一个标签  value为另一个标签以及两个标签公共被喜欢的次数

    public static final String ModelFileName = "src/main/resources/ml-25m/ratings2.csv";//模型数据的路径，需要提前从数据库中抽取出来
    public ItemBasedCF(int n_sim_tag,int n_rec_tag)
    {

      this.n_sim_tag = n_sim_tag;
        this.n_rec_tag = n_rec_tag;
        data_set = new HashMap<>();
        tag_popular = new HashMap<>();
        tag_map = new HashMap<>();
        user_map = new HashMap<>();
        tag_sim_matrix = new HashMap<>();

    }
    // 找到相似的20个标签，为目标用户推荐10个标签 默认
    public ItemBasedCF() {
        n_sim_tag = 20;
        n_rec_tag = 10;
        data_set = new HashMap<>();
        tag_popular = new HashMap<>();
        tag_map = new HashMap<>();
        user_map = new HashMap<>();
        tag_sim_matrix = new HashMap<>();
    }


    /**
     *  读取文件得到相关数据 数据格式为  userId,movieId,rating,timestamp
     */
    public  void get_dataset()
    {
         List<String> dataset = readCsvFile(ModelFileName);
        //读取出的csv文件为一行行的字符串  需要进行切分
        int tag_index = 0;
        int user_index = 0;
        for(String s :dataset)
        {

            String temp_list[] =  s.split(",");//userId,movieId,rating,timestamp
            String user_id = temp_list[0];
            String tag_id = temp_list[1];

            //建立 tag_id的映射  方便构建矩阵
            if(tag_map.containsKey(tag_id) == false)
            {
                tag_map.put(tag_id,tag_index);
                tag_index++;
            }
            double rating = Double.valueOf(temp_list[2]);
            //如果这个key以及存在 那么直接添加即可
            if(data_set.containsKey(user_id))
            {
                ArrayList<TagRating> tagRatings = data_set.get(user_id);
                tagRatings.add(new TagRating(tag_id,rating));
                data_set.put(user_id,tagRatings);
            }
            else
            {
                ArrayList<TagRating> tagRatings = new ArrayList<>();
                tagRatings.add(new TagRating(tag_id,rating));
                data_set.put(user_id,tagRatings);
                //建立 user_id的映射  方便构建矩阵
                tag_map.put(user_id,user_index);
                user_index++;

            }

        }

    }



    public void calc_movie_similarity()
    {
        Iterator<Map.Entry<String, ArrayList<TagRating>>> iterator = data_set.entrySet().iterator();
        //取出每个userid对应的tags列表
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<TagRating>> entry = iterator.next();

           String user_id = entry.getKey();
           ArrayList<TagRating> tagRatings = entry.getValue();
           //计算每个tag的点击率之和
           for( TagRating tagRating:tagRatings)
           {
               String tag_id = tagRating.getTag_id();
               double rating  = tagRating.getRating();
               //如果这个标签已经存在  则取出来+rating
               if(tag_popular.containsKey(tag_id))
               {
                  double sum_rating =  tag_popular.get(tag_id) +rating;
                  tag_popular.put(tag_id,sum_rating);

               }
               else
               {
                   //如果这个标签不存在
                   tag_popular.put(tag_id,rating);
               }
           }


        }
        tag_count = tag_popular.size();
//        tag_sim_matrix = new double[tag_count+1][tag_count+1];//初始化  标签相似矩阵


        invert_tag_map = new String [tag_count+1];//初始化逆向映射
        Iterator<Map.Entry<String, Integer>> iterator_map = tag_map.entrySet().iterator();
        while (iterator_map.hasNext()) {
            Map.Entry<String, Integer> entry = iterator_map.next();

            String user_id = entry.getKey();
            int index = entry.getValue();
            invert_tag_map[index] = user_id;
        }




        Iterator<Map.Entry<String, ArrayList<TagRating>>> iterator2 = data_set.entrySet().iterator();
        //取出每个userid对应的tags列表
        while (iterator2.hasNext()) {
            Map.Entry<String, ArrayList<TagRating>> entry = iterator2.next();

            String user_id = entry.getKey();
            ArrayList<TagRating> tagRatings = entry.getValue();

            //计算两个标签被用户同时喜欢的用户数量
            for(TagRating tagRating1:tagRatings)
            {
                for(TagRating tagRating2 :tagRatings)
                {
                    String tag_id1 = tagRating1.getTag_id();
                    String tag_id2 = tagRating2.getTag_id();
                    if(tag_id1.equals(tag_id2))
                        continue;
                    else
                    {
                        int index_tag_id1 = tag_map.get(tag_id1);
                        int index_tag_id2 = tag_map.get(tag_id2);
                        if(tag_sim_matrix.containsKey(tag_id1) == false) {
                            Map<String,Double> temp = new HashMap<>();
                            tag_sim_matrix.put(tag_id1,temp);
                        }
                        if(tag_sim_matrix.get(tag_id1).containsKey(tag_id2) == false)
                        {
                            tag_sim_matrix.get(tag_id1).put(tag_id2,1.0);
                        }
                        double a =  tag_sim_matrix.get(tag_id1).get(tag_id2) +1.0;
                        tag_sim_matrix.get(tag_id1).put(tag_id2,a);
                    }
                }
            }
        }

        //计算两个标签之间的相似性
        Iterator<Map.Entry<String, Map<String,Double>>> iterator_sim1 = tag_sim_matrix.entrySet().iterator();
        while (iterator_sim1.hasNext()) {
            Map.Entry<String, Map<String,Double>> entry = iterator_sim1.next();

            String tag_id1 = entry.getKey();
            Map<String,Double> tag_clicking = entry.getValue();
            Iterator<Map.Entry<String, Double>> iterator_sim2 = tag_clicking.entrySet().iterator();
            while (iterator_sim2.hasNext()) {
                Map.Entry<String, Double> entry2 = iterator_sim2.next();
                String tag_id2 = entry2.getKey();
                double clicking = entry2.getValue();
                if(tag_popular.get(tag_id1) == null )
                {
                    break;
                }
                if(tag_popular.get(tag_id2) == null )
                {
                    continue;
                }
                double a = tag_popular.get(tag_id1);
                double b =tag_popular.get(tag_id2);
                if(a == 0.0 ||b == 0)
                {
                    tag_sim_matrix.get(tag_id1).put(tag_id1,0.0) ; }
                else
                {
                    double result = tag_sim_matrix.get(tag_id1).get(tag_id2)/(Math.sqrt(a*b));
                    tag_sim_matrix.get(tag_id1).put(tag_id2,result);
                    }
            }

        }

          //计算两个标签之间的相似性
//        for(int i = 0 ; i < tag_sim_matrix.length ; i++)
//        {
//            for (int j = 0 ; j <tag_sim_matrix.length ; j++)
//            {
//                //如果某标签的用户数为0
//
//                if(tag_popular.get(invert_tag_map[i]) == null )
//                {
//                    break;
//                }
//                if(tag_popular.get(invert_tag_map[j]) == null )
//                {
//                   continue;
//                }
//                double a = tag_popular.get(invert_tag_map[i]);
//                double b =tag_popular.get(invert_tag_map[j]);
//                if(a == 0.0 ||b == 0)
//                {
//                        tag_sim_matrix[i][j] = 0; }
//                else
//                {
//                    tag_sim_matrix[i][j] = (int) (tag_sim_matrix[i][j]/(Math.sqrt(a*b))); }
//            }
//        }

    }


    private List<String> recommend(String user_id)
    {
        ArrayList<TagRating> watch_tags = data_set.get(user_id);
        Map<String,Double> rank = new HashMap<>();
        int index = 0;
        for(TagRating tagRating :watch_tags)
        {
            String tag_id = tagRating.getTag_id();
           Map<String,Double>  related_tags = tag_sim_matrix.get(tag_id);
            List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(related_tags.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    if(o2.getValue() > o1.getValue())
                        return 1;
                    else  if(o2.getValue() < o1.getValue())
                        return -1;
                    else return 0;
                }
            });

            for(Map.Entry<String, Double> t:list){
                if(in(watch_tags,t.getKey()))
                {
                    continue;
                }
                else
                {
                    if(rank.containsKey(t.getKey()) == false)
                    {
                        rank.put(t.getKey(),0.0);
                    }
                    double rate = rank.get(t.getKey());
                    rate += (t.getValue() * tagRating.getRating());
                    rank.put(t.getKey(),rate);
                    index++;
                    if(index >n_sim_tag) break;
                }

            }
        }
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(rank.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if(o2.getValue() > o1.getValue())
                    return 1;
                else  if(o2.getValue() < o1.getValue())
                    return -1;
                else return 0;
//                return (o2.getValue() - o1.getValue());
            }
        });
         index = 0;
        List<String> result = new ArrayList<>();
        for(Map.Entry<String, Double> t:list){
            result.add(t.getKey());
            if(index++ >n_rec_tag) break;
        }
        return result;

    }
    private boolean in(ArrayList<TagRating> watch_tags,String tag_id)
    {
        for( TagRating a : watch_tags)
        {
            if(a.getTag_id().equals(tag_id) )
            {
                return true;
            }
        }
        return false;
    }
    public static void main(String [] args)
    {
        ItemBasedCF i = new ItemBasedCF();
        i.get_dataset();
        i.calc_movie_similarity();
        List<String> result  = i.recommend("166");
        System.out.println(result.size());
    }
}
