package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import example.helloworld.HelloWorldAdapter;
import example.helloworld.HelloWorldData;

@Controller
@RequestMapping("helloworld/")
public class HelloWorldController
{

    @Autowired
    private HelloWorldAdapter helloWorldAdapter;

    @RequestMapping("list.do")
    @ResponseBody
    public List<HelloWorldData> list()
    {
        List<HelloWorldData> dataList = new ArrayList<>();
        dataList = helloWorldAdapter.getDataList();
        return dataList;
    }

    @RequestMapping("create.do")
    @ResponseBody
    public HelloWorldData create()
    {
        HelloWorldData data = new HelloWorldData();
        helloWorldAdapter.runWorkflow(data);
        return data;
    }

    @RequestMapping("send.do")
    @ResponseBody
    public Boolean send(String cid, String sender)
    {
        helloWorldAdapter.sendHelloWorld(cid, sender);
        return true;
    }

    @RequestMapping("reply.do")
    @ResponseBody
    public Boolean reply(String cid, String replier)
    {
        helloWorldAdapter.replyHelloWorld(cid, replier);
        return true;
    }

}
