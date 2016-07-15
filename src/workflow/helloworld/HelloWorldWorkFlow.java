package workflow.helloworld;

import java.util.UUID;

import org.copperengine.core.AutoWire;
import org.copperengine.core.Interrupt;
import org.copperengine.core.Response;
import org.copperengine.core.WaitMode;
import org.copperengine.core.WorkflowDescription;

import workflow.base.BasePersistentWorkflow;
import example.WorkflowDef;
import example.helloworld.HelloWorldAdapter;
import example.helloworld.HelloWorldData;

@WorkflowDescription(alias = WorkflowDef.HELLOWORLD, majorVersion = 1, minorVersion = 0, patchLevelVersion = 0)
public class HelloWorldWorkFlow extends BasePersistentWorkflow<HelloWorldData>
{

    private static final long serialVersionUID = 1L;

    private transient HelloWorldAdapter helloWorldAdapter;

    @AutoWire
    public void setHelloWorldAdapter(HelloWorldAdapter helloWorldAdapter)
    {
        this.helloWorldAdapter = helloWorldAdapter;
    }

    @Override
    public void runFlow() throws Interrupt
    {
        HelloWorldData data = getData();
        String correlationId;
        Response<HelloWorldData> response;

        correlationId = helloWorldAdapter.initHelloWorld(data);
        auditLog(LogLevel.INFO, "Init HelloWorld", "Done");

        wait(WaitMode.ALL, 5 * 60 * 60 * 1000, correlationId);
        response = getAndRemoveResponse(correlationId);

        auditLog(LogLevel.INFO, "Send HelloWorld", response.getResponse().getSender());

        wait(WaitMode.ALL, 5 * 60 * 60 * 1000, correlationId);
        response = getAndRemoveResponse(correlationId);

        auditLog(LogLevel.INFO, "Reply HelloWorld", response.getResponse().getReplier());
    }

    @Override
    protected String getTransactionId()
    {
        return UUID.randomUUID().toString();
    }

}
