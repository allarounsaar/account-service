package ee.allar.bank.typehandler;

import ee.allar.bank.common.MoneyUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(BigDecimal.class)
@MappedJdbcTypes(JdbcType.NUMERIC)
public class MonetaryAmountTypeHandler extends BaseTypeHandler<BigDecimal> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BigDecimal parameter, JdbcType jdbcType) throws SQLException {
        ps.setBigDecimal(i, MoneyUtils.toTwoDecimals(parameter));
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return MoneyUtils.toTwoDecimals(rs.getBigDecimal(columnName));
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return MoneyUtils.toTwoDecimals(rs.getBigDecimal(columnIndex));
    }

    @Override
    public BigDecimal getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return MoneyUtils.toTwoDecimals(cs.getBigDecimal(columnIndex));
    }
}
