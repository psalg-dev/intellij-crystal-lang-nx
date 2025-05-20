package org.intellij.crystal.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.crystal.CrystalLanguage;
import org.intellij.crystal.lang.lexer.CrystalLexerAdapter;
import org.intellij.crystal.lang.psi.CrystalFile;
import org.intellij.crystal.lang.psi.CrystalTokenSets;
import org.intellij.crystal.lang.psi.CrystalTypes;
import org.jetbrains.annotations.NotNull;

public class CrystalParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(CrystalLanguage.INSTANCE);


    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CrystalLexerAdapter();
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return CrystalTokenSets.COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiParser createParser(final Project project) {
        return new CrystalParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new CrystalFile(viewProvider);
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return CrystalTypes.Factory.createElement(node);
    }
}
