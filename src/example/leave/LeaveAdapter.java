package example.leave;

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
public class LeaveAdapter
{

    private static final Logger logger = LoggerFactory.getLogger(LeaveAdapter.class);

    private List<LeaveData> dataList = new ArrayList<>();

    @Autowired
    @Qualifier("persistent.engine")
    private ProcessingEngine engine;

    public void runWorkflow(LeaveData data)
    {
        try
        {
            engine.run(WorkflowDef.LEAVE, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String initLeave(LeaveData data)
    {
        String correlationId = "CID#" + System.currentTimeMillis();
        data.setCid(correlationId);
        data.setStatus(LeaveStatus.PEND_SEND);
        dataList.add(data);
        return correlationId;
    }

    public void sendLeave(String cid, String sender)
    {
        LeaveData data = fetchData(cid);
        if (data == null)
        {
            logger.info(cid + " LeaveWorkflow Error: Not Exists");
        }
        switch (data.getStatus())
        {
        case PEND_SEND:
            data.setStatus(LeaveStatus.PEND_REPLY);
            data.setSender(sender);
            Response<LeaveData> response = new Response<>(data.getCid(), data, null);
            Acknowledge.DefaultAcknowledge ack = new Acknowledge.DefaultAcknowledge();
            engine.notify(response, ack);
            ack.waitForAcknowledge();
            break;
        default:
            logger.info(cid + " LeaveWorkflow Error: No Permission");
        }
    }

    public void replyLeave(String cid, String replier)
    {
        LeaveData data = fetchData(cid);
        if (data == null)
        {
            logger.info(cid + " LeaveWorkflow Error: Not Exists");
        }
        switch (data.getStatus())
        {
        case PEND_REPLY:
            data.setStatus(LeaveStatus.DONE);
            data.setReplier(replier);
            Response<LeaveData> response = new Response<>(data.getCid(), data, null);
            Acknowledge.DefaultAcknowledge ack = new Acknowledge.DefaultAcknowledge();
            engine.notify(response, ack);
            ack.waitForAcknowledge();
            break;
        default:
            logger.info(cid + " LeaveWorkflow Error: No Permission");
        }
    }

    public List<LeaveData> getDataList()
    {
        return dataList;
    }

    private LeaveData fetchData(String cid)
    {
        if (cid == null)
            return null;

        for (LeaveData data : dataList)
        {
            if (cid.equals(data.getCid()))
            {
                return data;
            }
        }
        return null;
    }

}
