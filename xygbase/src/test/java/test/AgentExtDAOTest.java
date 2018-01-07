package test;

import com.keke.sanshui.base.admin.dao.AgentExtDAO;
import com.keke.sanshui.base.admin.po.agent.AgentExtPo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"classpath:application-context.xml"})
public class AgentExtDAOTest  {

    @Autowired
    AgentExtDAO agentExtDAO;
    @Test
    public void testInsert(){
        AgentExtPo agentExtPo = new AgentExtPo();
        agentExtPo.setAddCount(3);
        agentExtPo.setWeek(1);
        agentExtPo.setLastUpdateTime(System.currentTimeMillis());
        agentExtPo.setInsertTime(System.currentTimeMillis());
        agentExtPo.setPlayerId(1001);
        agentExtPo.setAgentId(2);
        agentExtPo.setIsAward(1);
        Assert.assertTrue(agentExtDAO.insertAgentExtPo(agentExtPo) > 0);
    }

    @Test
    public void testUpdate(){
        AgentExtPo updatePo = new AgentExtPo();
        updatePo.setAgentId(2);
        updatePo.setWeek(1);
        updatePo.setAddCount(33);
        agentExtDAO.updateAgentExtPo(updatePo);

        AgentExtPo agentExtPo =   agentExtDAO.selectByAgentId(2,1);
        Assert.assertTrue(agentExtPo != null && agentExtPo.getAddCount() == 33);
    }
}
