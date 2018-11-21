package main;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class FriendRecommendation extends Configured implements Tool {
   public static void main(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      int res = ToolRunner.run(new Configuration(), new FriendRecommendation(), args);
      
      System.exit(res);
   }
   
   public static class friend_mutualfriend extends LongWritable{
		long friend;
		long mutual;
		
		friend_mutualfriend(){
			friend = -1l;
			mutual = -1l;
		}
		friend_mutualfriend(long friend, long mutual){
			this.friend = friend;
			this.mutual = mutual;
		}
		
		@Override
		public void write(DataOutput out) throws IOException{
			out.writeLong(friend);
			out.writeLong(mutual);
		}
		
		@Override
		public void readFields(DataInput in) throws IOException{
			friend = in.readLong();
			mutual = in.readLong();
		}
		@Override
		public String toString(){
			return " friend: " + friend + " mutual: " + mutual;
		}
		
		
		public long get_friend(){
			return friend;
		}
		public long get_mutual(){
			return mutual;
		}
	}

   @Override
   public int run(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      Job job = new Job(getConf(), "FriendRecommendation");
      job.setJarByClass(FriendRecommendation.class);
      job.setOutputKeyClass(LongWritable.class);
      job.setOutputValueClass(friend_mutualfriend.class);

      job.setMapperClass(Map.class);
      job.setReducerClass(Reduce.class);

      job.setInputFormatClass(TextInputFormat.class);
      job.setOutputFormatClass(TextOutputFormat.class);

      FileInputFormat.addInputPath(job, new Path(args[0]));
      FileOutputFormat.setOutputPath(job, new Path(args[1]));

      job.waitForCompletion(true);
      
      return 0;
   }
   
   public static class Map extends Mapper<LongWritable, Text, LongWritable, friend_mutualfriend> {
//     private final static LongWritable ONE = new LongWritable(1L);
	   
     @Override
     public void map(LongWritable key, Text value, Context context)
             throws IOException, InterruptedException {
   	  String temp[] = value.toString().split("\t");
   	  long user = Long.parseLong(temp[0]);
   	  
   	 if(temp.length == 2){
   		 String[] friends = temp[1].split(",");
//   	  for(int i = 0; i < friends.length; i++){
//   		  System.out.print(friends[i]+",");  
//   	  }
//   	  System.out.println();
   	  
   	  
   		 for(int i = 0; i < friends.length; i++){
   			 context.write(new LongWritable(user), new friend_mutualfriend(Long.parseLong(friends[i]),-1l));
   			 for(int j = i+1; j < friends.length; j++){
   				 context.write(new LongWritable(Long.parseLong(friends[i])), new friend_mutualfriend(Long.parseLong(friends[j]),user));
   				 context.write(new LongWritable(Long.parseLong(friends[j])), new friend_mutualfriend(Long.parseLong(friends[i]),user));
   			 }
   		 }
   	 }
     }
  }


   public static class Reduce extends Reducer<LongWritable, friend_mutualfriend, LongWritable, Text> {	   
	  @Override
      public void reduce(LongWritable key, Iterable<friend_mutualfriend> values, Context context)
              throws IOException, InterruptedException {
    	  long friend;
    	  long mutual;
    	  HashMap<Long,Long> recommends = new HashMap<Long,Long>();
    	  for(friend_mutualfriend val : values){
    		  friend = val.get_friend();
    		  mutual = val.get_mutual();
//    		  System.out.println(key + ","+friend+","+mutual);
    		  if(recommends.containsKey(friend)){
    			  if(mutual == -1l){
//    				  System.out.println("INSIDE!"+key + ","+friend+","+mutual);
    				  recommends.put(friend, -1l);
    			  }
    			  else{
    				  if(recommends.get(friend) != -1l){
    					  long times = recommends.get(friend) + 1;
    					  recommends.put(friend, times);
    				  }
    			  }
    		  }
    		  else{
    			  if(mutual == -1l) 
    				  recommends.put(friend, -1l);
    			  else
    				  recommends.put(friend, 1l);
    		  }
    	  }
    	  
//    	  System.out.println(key);
//    	  
//    	  for(long k : recommends.keySet())
//    	  {
//    		  System.out.println(k+"="+ recommends.get(k));
//    	  }
//    	  System.out.println();
    	  
    	  ArrayList<Long> recommend_friend = new ArrayList<Long>();
    	  long max = 0;
    	  long f = -1;
    	  for(int i = 0; i < 10 && i < recommends.size(); i++){
    		  for(Long k : recommends.keySet()){
    			  if(recommends.get(k) > max){
    				  max = recommends.get(k);
    				  f = k;
    			  }
    		  }
    		  if(f == -1){
    			  break;
    		  }
    		  else{
    			  recommend_friend.add(f);
    			  recommends.put(f, -1l);
    			  f = -1;
    			  max = 0;
    		  }
    	  }
    	  
    	  String output = "";
    	  for(int i = 0; i < recommend_friend.size(); i++){
    		  output = output + recommend_friend.get(i) + ",";
    	  }
    	  if(recommend_friend.size() > 0){
    		  context.write(key, new Text(output.substring(0, output.length()-1)));
    	  }
    	  else{
    		  context.write(key, new Text(""));
    	  }
      }
   }
}
