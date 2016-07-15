package workflow.leave;

import java.util.UUID;

import org.copperengine.core.AutoWire;
import org.copperengine.core.Interrupt;
import org.copperengine.core.Response;
import org.copperengine.core.WaitMode;
import org.copperengine.core.WorkflowDescription;

import workflow.base.BasePersistentWorkflow;
import example.WorkflowDef;
import example.leave.LeaveAdapter;
import example.leave.LeaveData;

@WorkflowDescription(alias = WorkflowDef.LEAVE, majorVersion = 1, minorVersion = 0, patchLevelVersion = 0)
public class LeaveWorkFlow extends BasePersistentWorkflow<LeaveData>
{

    private static final long serialVersionUID = 1L;

    private transient LeaveAdapter leaveAdapter;

    @AutoWire
    public void setLeaveAdapter(LeaveAdapter leaveAdapter)
    {
        this.leaveAdapter = leaveAdapter;
    }

    @Override
    public void runFlow() throws Interrupt
    {
        LeaveData data = getData();
        String correlationId;
        Response<LeaveData> response;

        correlationId = leaveAdapter.initLeave(data);
        auditLog(LogLevel.INFO, "Init Leave", "Done");

        wait(WaitMode.ALL, 5 * 60 * 60 * 1000, correlationId);
        response = getAndRemoveResponse(correlationId);

        auditLog(LogLevel.INFO, "Send Leave", response.getResponse().getSender());

        wait(WaitMode.ALL, 5 * 60 * 60 * 1000, correlationId);
        response = getAndRemoveResponse(correlationId);

        auditLog(LogLevel.INFO, "Reply Leave", response.getResponse().getReplier());
    }

    @Override
    protected String getTransactionId()
    {
        return UUID.randomUUID().toString();
    }
}
