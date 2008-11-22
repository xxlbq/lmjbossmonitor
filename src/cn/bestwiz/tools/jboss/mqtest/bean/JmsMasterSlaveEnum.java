package cn.bestwiz.tools.jboss.mqtest.bean;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.enums.ValuedEnum;

/**
 * JmsMasterSlaveEnum USING JhfJmsProvider 
 * 
 * @author JHF Team <jhf@bestwiz.cn>
 * 
 * @copyright 2006, BestWiz(Dalian) Co.,Ltd
 * @version $Id: JmsMasterSlaveEnum.java,v 1.2 2007/11/07 06:03:47 panxy Exp $
 */
public final class JmsMasterSlaveEnum extends ValuedEnum {

    private static final long serialVersionUID = -6783546734627469052L;

    private static final int NOT_USING = 0;
    
    private static final int MASTER = 1;

    private static final int SLAVE = 2;

    public static final JmsMasterSlaveEnum NOT_USING_ENUM = new JmsMasterSlaveEnum("NOT_USING", NOT_USING);
    
    public static final JmsMasterSlaveEnum MASTER_ENUM = new JmsMasterSlaveEnum("MASTER", MASTER);

    public static final JmsMasterSlaveEnum SLAVE_ENUM = new JmsMasterSlaveEnum("SLAVE", SLAVE);

    private JmsMasterSlaveEnum(String name, int value) {
        super(name, value);
    }

    public static JmsMasterSlaveEnum getEnum(String bool) {
        return (JmsMasterSlaveEnum) getEnum(JmsMasterSlaveEnum.class, bool);
    }

    public static JmsMasterSlaveEnum getEnum(int bool) {
        return (JmsMasterSlaveEnum) getEnum(JmsMasterSlaveEnum.class, bool);
    }

    public static Map getEnumMap() {
        return getEnumMap(JmsMasterSlaveEnum.class);
    }

    public static List getEnumList() {
        return getEnumList(JmsMasterSlaveEnum.class);
    }

    public static Iterator iterator() {
        return iterator(JmsMasterSlaveEnum.class);
    }

}
