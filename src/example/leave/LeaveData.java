package example.leave;

import com.omniselling.wf.data.base.BaseWorkflowData;

public class LeaveData extends BaseWorkflowData
{

    private static final long serialVersionUID = -190879447387202081L;

    private String cid;
    private LeaveStatus status;
    private String sender;
    private String replier;

    public String getCid()
    {
        return cid;
    }

    public void setCid(String cid)
    {
        this.cid = cid;
    }

    public LeaveStatus getStatus()
    {
        return status;
    }

    public void setStatus(LeaveStatus status)
    {
        this.status = status;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getReplier()
    {
        return replier;
    }

    public void setReplier(String replier)
    {
        this.replier = replier;
    }

}
