package im.eg.common.exception;

import im.eg.common.result.R;
import im.eg.common.result.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UnifiedExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return R.error();
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public R badSqlGrammarException(BadSqlGrammarException ex) {
        log.error(ex.getMessage(), ex);
        return R.setResult(ResponseEnum.BAD_SQL_GRAMMAR_ERROR);
    }
}
