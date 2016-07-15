package example.helloworld;

import java.util.ArrayList;
import java.util.List;

import org.copperengine.core.Acknowledge;
import org.copperengine.core.ProcessingEngine;
import org.copperengine.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import example.WorkflowDef;

@Service
public class HelloWorldAdapter
{
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldAdapter.class);

    private List<HelloWorldData> dataList = new ArrayList<>();

    @Autowired
    @Qualifier("transient.engine")
    private ProcessingEngine engine;

    public void runWorkflow(HelloWorldData data)
    {
        try
        {
            engine.run(WorkflowDef.HELLOWORLD, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String initHelloWorld(HelloWorldData data)
    {
        String correlationId = "CID#" + System.currentTimeMillis();
        data.setCid(correlationId);
        data.setStatus(HelloWorldStatus.PEND_SEND);
        dataList.add(data);
        return correlationId;
    }

    public void sendHelloWorld(String cid, String sender)
    {
        HelloWorldData data = fetchData(cid);
        if (data == null)
        {
            logger.info(cid + " HelloWorldWorkflow Error: Not Exists");
        }
        switch (data.getStatus())
        {
        case PEND_SEND:
            data.setStatus(HelloWorldStatus.PEND_REPLY);
            data.setSender(sender);
            Response<HelloWorldData> response = new Response<>(data.getCid(), data, null);
            Acknowledge.DefaultAcknowledge ack = new Acknowledge.DefaultAcknowledge();
            engine.notify(response, ack);
            ack.waitForAcknowledge();
            break;
        default:
            logger.info(cid + " HelloWorldWorkflow Error: No Permission");
        }
    }

    public void replyHelloWorld(String cid, String replier)
    {
        HelloWorldData data = fetchData(cid);
        if (data == null)
        {
            logger.info(cid + " HelloWorldWorkflow Error: Not Exists");
        }
        switch (data.getStatus())
        {
        case PEND_REPLY:
            data.setStatus(HelloWorldStatus.DONE);
            data.setReplier(replier);
            Response<HelloWorldData> response = new Response<>(data.getCid(), data, null);
            Acknowledge.DefaultAcknowledge ack = new Acknowledge.DefaultAcknowledge();
            engine.notify(response, ack);
            ack.waitForAcknowledge();
            break;
        default:
            logger.info(cid + " HelloWorldWorkflow Error: No Permission");
        }
    }

    public List<HelloWorldData> getDataList()
    {
        return dataList;
    }

    private HelloWorldData fetchData(String cid)
    {
        if (cid == null)
            return null;

        for (HelloWorldData data : dataList)
        {
            if (cid.equals(data.getCid()))
            {
                return data;
            }
        }
        return null;
    }

}
