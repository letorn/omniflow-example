package test;

import org.copperengine.core.ProcessingEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring-workflow.xml" })
public class TestWorkflow
{

    @Autowired
    @Qualifier("persistent.engine")
    private ProcessingEngine engine;

    @Test
    public void test()
    {
        System.out.println("engine: " + engine);
    }

}
