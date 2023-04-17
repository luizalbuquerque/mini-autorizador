package miniautorizador.util;

import org.springframework.http.HttpStatus;

public class ConstantUtils {

    private ConstantUtils() {
        throw new UnsupportedOperationException();
    }
    public static final String CARTAO_DUPLICADO = "{cartao.validacao.duplicado}";
    public static final String CARD_NOT_FOUND = "{card.not.found}";
    public static final String TRANSACTION_NOT_FOUND = "{transaction.not.found}";

    public static final String TRANSACAO_NAO_ENCONTRADO = "{transaco.nao.encontrado}";



}
