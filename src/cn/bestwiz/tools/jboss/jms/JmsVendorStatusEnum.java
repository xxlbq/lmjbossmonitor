package cn.bestwiz.tools.jboss.jms;

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
 * @version $Id: JmsVendorStatusEnum.java,v 1.2 2007/11/07 06:03:47 panxy Exp $
 */
public final class JmsVendorStatusEnum extends ValuedEnum {

    private static final long serialVersionUID = -6783546734627469055L;

    private static final int UNKNOWN = 0;
    
    private static final int RUNNING = 1;

    private static final int DOWN = 2;

    public static final JmsVendorStatusEnum UNKNOWN_ENUM = new JmsVendorStatusEnum("UNKNOWN", UNKNOWN);
    
    public static final JmsVendorStatusEnum RUNNING_ENUM = new JmsVendorStatusEnum("RUNNING", RUNNING);

    public static final JmsVendorStatusEnum DOWN_ENUM = new JmsVendorStatusEnum("DOWN", DOWN);

    private JmsVendorStatusEnum(String name, int value) {
        super(name, value);
    }

    public static JmsVendorStatusEnum getEnum(String bool) {
        return (JmsVendorStatusEnum) getEnum(JmsVendorStatusEnum.class, bool);
    }

    public static JmsVendorStatusEnum getEnum(int bool) {
        return (JmsVendorStatusEnum) getEnum(JmsVendorStatusEnum.class, bool);
    }

    public static Map getEnumMap() {
        return getEnumMap(JmsVendorStatusEnum.class);
    }

    public static List getEnumList() {
        return getEnumList(JmsVendorStatusEnum.class);
    }

    public static Iterator iterator() {
        return iterator(JmsVendorStatusEnum.class);
    }

}
