package com.vikal.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class wordCount {
	
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>{
		
		public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter report) 
		throws IOException {
			
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			
			Text word = new Text();
			final IntWritable one = new IntWritable(1);
			
			while(tokenizer.hasMoreTokens()){
				word.set(tokenizer.nextToken());
				output.collect(word, one);
			}
			
		}
	}
	
	public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>{
		
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter report)
		throws IOException{
			
			int sum = 0;
			while(values.hasNext()){
				sum += values.next().get();
			}
			output.collect(key, new IntWritable(sum));
			}
		}



   public static void main(String[] args) throws Exception{
	   
	   JobConf conf = new JobConf(wordCount.class);
	   
	   conf.setOutputKeyClass(Text.class);
	   conf.setOutputValueClass(IntWritable.class);
	   
	   conf.setInputFormat(TextInputFormat.class);
	   conf.setMapperClass(Map.class);
	   conf.setCombinerClass(Reduce.class);
	   conf.setReducerClass(Reduce.class);
	   conf.setOutputFormat(TextOutputFormat.class);
	   
	   FileInputFormat.setInputPaths(conf, new Path(args[0]));
	   FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	   
	   JobClient.runJob(conf);
	
   }
}