package com.zhangfuwen.info;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dean on 4/8/17.
 */
@Entity
@Table(name="t_warning")
public class Warning {
    public static final int WARN_TYPE_UPPER_LIMIT=0;
    public static final int WARN_TYPE_LOWER_LIMIT=1;
    public static final int WARN_STATUS_NEW=1;
    public static final int WARN_STATUS_OUTDATED=0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="threshold_id")
    Long thresholdId;

    @Column(name="warn_type")
    int type;

    @Column(name="warn_status")
    int status;

    @Column(name="created")
    Timestamp created;

    @Column(name="closed")
    Timestamp closed;

    @Column(name="readout_id")
    Long readoutId;
}
