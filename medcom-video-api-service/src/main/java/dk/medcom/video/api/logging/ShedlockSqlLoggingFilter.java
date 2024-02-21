package dk.medcom.video.api.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ShedlockSqlLoggingFilter extends Filter<ILoggingEvent> {
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLoggerName().equals("net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateStorageAccessor") &&
                event.getThrowableProxy() != null &&
                event.getThrowableProxy().getCause() != null &&
                event.getThrowableProxy().getCause().getClassName() != null &&
                event.getThrowableProxy().getCause().getClassName().equals("java.sql.SQLTransactionRollbackException")) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
