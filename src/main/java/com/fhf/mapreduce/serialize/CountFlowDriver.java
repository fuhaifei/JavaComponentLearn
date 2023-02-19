package com.fhf.mapreduce.serialize;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;


public class CountFlowDriver {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();

        //1.初始化配置和输入输出路径
        String[] otherArgs = (new GenericOptionsParser(conf, args)).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage:  <in> [<in>...] <out>");
            System.exit(2);
        }
        //2.注册job
        Job job = Job.getInstance(conf, "count flow");
        //3.设置job jar包
        job.setJarByClass(CountFlowDriver.class);
        //4.设计map和reduce
        job.setMapperClass(CountFlowMapper.class);
        job.setReducerClass(CountFlowReducer.class);
        //5.设置mapper输出格式
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);
        //6.设置reducer输出格式
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //7.设置输入输出路径
        for(int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[otherArgs.length - 1]));
//        FileInputFormat.addInputPath(job, new Path("C:\\Users\\79351\\OneDrive\\桌面\\正在写的文档\\hadoop学习笔记\\笔记（word版本）\\phone_data.txt"));
//        FileOutputFormat.setOutputPath(job, new Path("C:\\Users\\79351\\OneDrive\\桌面\\正在写的文档\\hadoop学习笔记\\笔记（word版本）\\phone_out.txt"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class CountFlowMapper extends Mapper<Object, Text, Text, FlowBean>{
        //持有一个装载数据的对象
        FlowBean flowBean = new FlowBean();
        Text outKey = new Text();
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] data = value.toString().split("\t");
            System.out.print("[");
            for(String str:data){
                System.out.print(str);
            }
            System.out.println("]");
            flowBean.setUpStream(Integer.parseInt(data[data.length - 3]));
            flowBean.setDownStream(Integer.parseInt(data[data.length - 2]));
            outKey.set(data[1]);
            context.write(outKey, flowBean);
        }
    }

    public static class CountFlowReducer extends Reducer<Text, FlowBean, Text, Text>{
        Text outRes = new Text();
        @Override
        protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
            FlowBean outValue = null;
            for(FlowBean value:values){
                if(outValue == null){
                    outValue = value;
                }else{
                    outValue.setUpStream(outValue.getUpStream() + value.getUpStream());
                }
            }
            assert outValue != null;
            outRes.set(outValue.toString());
            context.write(key, outRes);
        }
    }
}
