select
AUXILIARY_CONFIG_VERSION_ID,
CML_BUY,
CML_SELL,
DATETIME,
DISBALANCE,
EXCHANGE_ID,
LIQUIDITY_BUY,
LIQUIDITY_CONFIG_VERSION_ID,
LIQUIDITY_SELL,
MARKET_CATEGORY,
SECURITY_ID
from sc_liquidity
where
--market_category='CUR_SPOT'
security_id=(select security_id from tl_security where shortname='USDRUB_TOM' and board_id=(select board_id from tl_boards where ident='CETS'))
and session_id=(select id from v_trade_session where exchange_id=1002 and trunc(begin_datetime)=to_date(?,'yyyyMMdd'))
and DATETIME>to_timestamp(?,'yyyy-mm-dd hh24:mi:ss.ff9')
order by DATETIME


