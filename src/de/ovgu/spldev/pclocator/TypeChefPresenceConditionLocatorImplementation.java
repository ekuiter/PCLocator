package de.ovgu.spldev.pclocator;

import de.fosd.typechef.options.FrontendOptionsWithConfigFiles;
import de.fosd.typechef.parser.TokenReader;
import de.fosd.typechef.parser.c.CToken;
import de.fosd.typechef.parser.c.CTypeContext;
import de.fosd.typechef.parser.c.TokenPosition;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Does not actually parse the C code, only uses the partial preprocessor.
 */
public class TypeChefPresenceConditionLocatorImplementation extends LegacyTypeChefPresenceConditionLocatorImplementation {
    public String getName() {
        return "TypeChef";
    }

    protected Object run(FrontendOptionsWithConfigFiles opt) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TokenReader<CToken, CTypeContext>> future = executor.submit(
                () -> lex(opt));
        executor.shutdown();
        return future.get();
    }

    protected void process(Object artifact, LineSupplier lineSupplier) {
        TokenReader<CToken, CTypeContext> tokenReader = (TokenReader<CToken, CTypeContext>) artifact;
        int line = lineSupplier.next();

        if (line != -1)
            while (!tokenReader.atEnd()) {
                CToken token = tokenReader.first();
                TokenPosition position = token.getPosition();
                if (position.getFile().equals("file " + _filePath.toString())) {
                    line = lineSupplier.catchUp(position.getLine());
                    if (position.getLine() == line) {
                        putPresenceCondition(line, token.getFeature());
                        if ((line = lineSupplier.next()) == -1)
                            break;
                    }
                }
                tokenReader = tokenReader.rest();
            }

        while (lineSupplier.next() != -1) ;
    }
}
