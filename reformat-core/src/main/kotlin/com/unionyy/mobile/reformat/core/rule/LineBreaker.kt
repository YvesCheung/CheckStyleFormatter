package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatContext
import com.unionyy.mobile.reformat.core.FormatRule
import com.unionyy.mobile.reformat.core.Location
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.java.JavaLanguage
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.ASTERISK
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.COMMA
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.C_STYLE_COMMENT
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.DIV
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.DOT
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.END_OF_LINE_COMMENT
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.EQ
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.LBRACE
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.LPARENTH
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.MINUS
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.PLUS
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.RBRACE
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.RPARENTH
import org.jetbrains.kotlin.com.intellij.psi.JavaTokenType.STRING_LITERAL
import org.jetbrains.kotlin.com.intellij.psi.PsiArrayInitializerExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiCodeBlock
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiDeclarationStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionList
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiIfStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiLocalVariable
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiPolyadicExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiReferenceExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.CompositeElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.FileElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.BINARY_EXPRESSION
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.EXTENDS_LIST
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.FIELD
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.IMPLEMENTS_LIST
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.JAVA_CODE_REFERENCE
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.LITERAL_EXPRESSION
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.JavaElementType.PARAMETER
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiCoreCommentImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.ClassElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.ParameterListElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.psi.psiUtil.children
import java.lang.StringBuilder

class LineBreaker : FormatRule {

    companion object {

        private const val maxLineLength = 120

        private const val lineBreak = "\n"

        private const val indent = "    "

        private const val SCAN_A = 1
        private const val SCAN_B = 2
        private const val SCAN_C = 3
        private const val SCAN_D = 4
        private const val SCAN_E = 5

        private fun lineBreak(
            context: FormatContext,
            lineStart: Int,
            moreIndent: String = ""
        ): ASTNode {
            val startNode = context.fileContent.psi.findElementAt(lineStart)?.node
                ?: return PsiWhiteSpaceImpl(lineBreak)
            return lineBreak(startNode, moreIndent)
        }

        private fun lineBreak(
            startNode: ASTNode,
            moreIndent: String = ""
        ): ASTNode {
            var lastIndent = ""
            if (startNode is PsiWhiteSpace) {
                lastIndent = getIndent((startNode as PsiWhiteSpace).text)
            } else if (startNode.treePrev is PsiWhiteSpace) {
                lastIndent = getIndent((startNode.treePrev as PsiWhiteSpace).text)
            } else if (startNode.treePrev == null && startNode.treeParent != null) {
                return lineBreak(startNode.treeParent, moreIndent)
            }
            return PsiWhiteSpaceImpl(lineBreak + lastIndent + moreIndent)
        }

        private fun getIndent(txt: String): String {
            val idx = txt.lastIndexOf(lineBreak)
            return if (idx in 0..txt.length - 2) {
                txt.substring(idx + 1)
            } else {
                txt
            }
        }
    }

    private data class Line(
        val lineNum: Int,
        val start: Int,
        val end: Int,
        val exceed: Boolean,
        val txt: String,
        val line: Line? = null
    )

    //map lineNum -> line(startOffset, endOffset)
    private val lines = mutableMapOf<Int, Line>()

    private val toBeLineBreak = mutableListOf<LineBreakAction>()

    private interface LineBreakAction {

        fun run(context: FormatContext)

        fun report(context: FormatContext)
    }

    override fun beforeVisit(context: FormatContext) {
        super.beforeVisit(context)
        lines.clear()
        toBeLineBreak.clear()
    }

    @Suppress("CascadeIf")
    override fun visit(context: FormatContext, node: ASTNode) {
        if (context.language != JavaLanguage.INSTANCE) {
            return
        }
        if (node is FileElement) {
            visitWholeFile(node)
        } else {

            if (lines.isEmpty()) {
                return
            }

            val location = context.getCodeLocation(node)
            val line = lines.getValue(location.line)

            when (context.scanningTimes) {
                SCAN_A -> {
                    if (node is ParameterListElement) {
                        breakFunctionParam(context, node, line)
                    } else if (node is PsiComment) {
                        breakComment(context, node, line)
                    } else if (node is PsiIfStatement) {
                        breakIfStatement(context, node, line)
                    } else if (node is ClassElement) {
                        breakClassDefine(context, node, line)
                    }
                }
                SCAN_B -> {
                    if (node is PsiExpressionList) {
                        breakFunctionCallParamList(context, node, line)
                    } else if (node.elementType == EQ) {
                        breakFieldOrVariable(context, node, line)
                    }
                }
                SCAN_C -> {
                    if (node.elementType == JavaTokenType.QUEST ||
                        node.elementType == JavaTokenType.COLON) {
                        breakQuest(context, node, line)
                    } else if (node.elementType == STRING_LITERAL) {
                        breakStringLiteral(context, node, line)
                    } else if (node is PsiPolyadicExpression) {
                        breakPolyadicOperator(context, node, line)
                    } else if (node is PsiArrayInitializerExpression) {
                        breakArrayInitializer(context, node, line)
                    }
                }
                SCAN_D -> {
                    if (node is PsiPolyadicExpression) {
                        breakPolyadicOperator(context, node, line)
                    }
                }
                SCAN_E -> {
                    if (node.elementType == DOT) {
                        breakDot(context, node, line, location)
                    }
                }
            }
        }
    }

    private fun breakFunctionParam(
        context: FormatContext,
        node: ASTNode,
        line: Line
    ) {
        val paramNum = node.children().count {
            it.elementType == PARAMETER
        }
        if (paramNum > 4 || line.exceed) {
            node.getChildren(null).forEach { child ->
                if (child.elementType == COMMA ||
                    child.elementType == LPARENTH) {
                    val whiteSpaceExpect = child.treeNext
                    toBeLineBreak.add(
                        NormalLineBreak(
                            whiteSpaceExpect,
                            lineBreak(context, line.start, indent),
                            "the token '(' or ',' in a parameter list: ${node.text}."
                        )
                    )
                } else if (child.elementType == RPARENTH) {
                    toBeLineBreak.add(
                        NormalLineBreak(
                            child,
                            lineBreak(context, line.start),
                            "the token ')' in a parameter list: ${node.text}."
                        )
                    )
                }
            }
        }
    }

    private fun breakFunctionCallParamList(
        context: FormatContext,
        node: ASTNode,
        line: Line
    ) {
        val paramNum = node.children().count {
            it.elementType == COMMA
        }
        if (line.exceed && paramNum > 0) {
            node.getChildren(null).forEach { child ->
                if (child.elementType == COMMA ||
                    child.elementType == LPARENTH) {
                    val whiteSpaceExpect = child.treeNext
                    toBeLineBreak.add(
                        NormalLineBreak(
                            whiteSpaceExpect,
                            lineBreak(context, line.start, getRealIndent(node, "")),
                            "the token '(' or ',' in a expression: ${node.text}."
                        )
                    )
                } else if (child.elementType == RPARENTH) {
                    toBeLineBreak.add(
                        NormalLineBreak(
                            child,
                            lineBreak(context, line.start,
                                getRealIndent(node, "").substring(4)),
                            "the token ')' in a expression: ${node.text}."
                        )
                    )
                }
            }
        }
    }

    private fun breakArrayInitializer(
        context: FormatContext,
        node: ASTNode,
        line: Line
    ) {
        if (line.exceed) {
            node.getChildren(null).forEach { child ->
                if (child.elementType == COMMA ||
                    child.elementType == LBRACE) {
                    val whiteSpaceExpect = child.treeNext
                    toBeLineBreak.add(
                        NormalLineBreak(
                            whiteSpaceExpect,
                            lineBreak(context, line.start, indent),
                            "the token '{' or ',' in a expression: ${node.text}."
                        )
                    )
                } else if (child.elementType == RBRACE) {
                    toBeLineBreak.add(
                        NormalLineBreak(
                            child,
                            lineBreak(context, line.start),
                            "the token '}' in a expression: ${node.text}."
                        )
                    )
                }
            }
        }
    }

    @Suppress("CascadeIf")
    private fun getRealIndent(node: ASTNode?, actualIndent: String): String {
        return if (node == null) {
            actualIndent
        } else if (node is PsiCodeBlock) {
            actualIndent
        } else if (node is PsiExpressionList) {
            getRealIndent(node.treeParent, actualIndent + indent)
        } else {
            getRealIndent(node.treeParent, actualIndent)
        }
    }

    private fun breakPolyadicOperator(
        context: FormatContext,
        node: ASTNode, //PsiPolyadicExpression
        line: Line
    ) {
        if (line.exceed) {
            for (child in node.getChildren(null)) {
                if (child.elementType == PLUS ||
                    child.elementType == MINUS ||
                    child.elementType == ASTERISK ||
                    child.elementType == DIV) {
                    val maybeWhiteSpace = child.treeNext
                    val next: ASTNode =
                        (if (maybeWhiteSpace is PsiWhiteSpace) {
                            if (maybeWhiteSpace.textContains('\n')) {
                                continue
                            }
                            maybeWhiteSpace.treeNext
                        } else {
                            maybeWhiteSpace
                        }) ?: continue

                    if (context.scanningTimes == SCAN_C) {
                        if (next.elementType != LITERAL_EXPRESSION ||
                            next.getChildren(null).firstOrNull()?.elementType != STRING_LITERAL) {
                            continue
                        }
                    } else {
                        if (next.elementType == LITERAL_EXPRESSION &&
                            next.getChildren(null).firstOrNull()?.elementType != STRING_LITERAL) {
                            continue
                        }
                    }

                    toBeLineBreak.add(
                        NormalLineBreak(
                            next,
                            lineBreak(context, line.start, indent),
                            "operator '${child.elementType}' " +
                                "in the expression: ${node.text}."))
                }
            }
        }
    }

    private fun breakFieldOrVariable(
        context: FormatContext,
        node: ASTNode, //EQ
        line: Line
    ) {
        if (line.exceed) {
            val parent = node.treeParent ?: return
            if (parent.elementType == FIELD ||
                parent is PsiLocalVariable) {

                toBeLineBreak.add(
                    NormalLineBreak(
                        node.treeNext,
                        lineBreak(context, line.start, indent + indent),
                        "token '=' in the field or variable: ${parent.text}."
                    )
                )
            }
        }
    }

    private fun breakComment(
        context: FormatContext,
        node: ASTNode,
        line: Line
    ) {
        if (line.exceed && node.elementType == END_OF_LINE_COMMENT) {
            val parent = node.treeParent
            if (parent != null) {
                val preNode = node.treePrev
                val isLineStart =
                    (preNode is PsiWhiteSpace && preNode.textContains('\n')) ||
                        node.startOffset == line.start
                if (preNode != null && !isLineStart) {
                    if (parent.elementType == FIELD ||
                        parent is PsiDeclarationStatement ||
                        parent is PsiExpressionStatement) {
                        toBeLineBreak.add(
                            MoveCommentToStart(
                                node,
                                parent,
                                lineBreak(context, parent.startOffset - 1))
                        )
                    } else {
                        toBeLineBreak.add(
                            NormalLineBreak(
                                node,
                                lineBreak(context, line.start),
                                "move the end of line comment to a new line: ${node.text}.")
                        )
                    }
                }
            }

            toBeLineBreak.add(CutEndOfLineComment(node))
        } else if (node.elementType == C_STYLE_COMMENT) {
            toBeLineBreak.add(CutCStyleComment(node))
        }
    }

    private fun breakIfStatement(
        context: FormatContext,
        node: ASTNode,
        line: Line
    ) {
        fun ASTNode?.doBreak() {
            val target = this ?: return
            toBeLineBreak.add(
                NormalLineBreak(
                    target,
                    lineBreak(context, line.start, indent + indent),
                    "operator '${target.elementType}' in 'if' statement.")
            )
        }
        //if 语句判断 or else if
        if (line.exceed) {
            val ifState = node.findChildByType(BINARY_EXPRESSION) ?: return
            val target = listOf<IElementType>(
                JavaTokenType.OROR, JavaTokenType.ANDAND, JavaTokenType.OR, JavaTokenType.AND
            )
            target.forEach { ifState.findChildByType(it).doBreak() }
        }
    }

    private fun breakQuest(context: FormatContext, node: ASTNode, line: Line) {
        //三目运算符里的逗号和冒号
        if (line.exceed) {
            val parentText =
                if (node.treeParent != null) {
                    " in the expression: " + node.treeParent + "."
                } else {
                    "."
                }
            toBeLineBreak.add(
                NormalLineBreak(
                    node,
                    lineBreak(context, line.start, indent + indent),
                    "ternary operator: ' ? : '$parentText"
                )
            )
        }
    }

    private fun breakDot(context: FormatContext, node: ASTNode, line: Line, location: Location) {
        //处理引用与方法调用的区别
        if (line.exceed) {
            val parent = node.treeParent
            val grandlParent = node.treeParent.treeParent
            if (grandlParent != null && ((grandlParent is PsiReferenceExpression)
                    || (parent.elementType == JAVA_CODE_REFERENCE))) {
                val column = location.column
                val parentText = (parent as ASTNode).text
                val nextParentTextLength = parent.treeNext?.textLength ?: 0
                val totalLength = column + nextParentTextLength +
                    parentText.split(".")[1].length
                if (totalLength <= 119) {
                    return
                }
                toBeLineBreak.add(
                    NormalLineBreak(
                        node,
                        lineBreak(context, line.start, indent + indent),
                        "'.' in the reference expression: $parentText."
                    )
                )
            } else if (grandlParent != null && (grandlParent is PsiMethodCallExpression)) {
                val column = location.column
                val parentText = (parent as ASTNode).text
                val nextParentTextLength = parent.treeNext?.textLength ?: 0
                val totalLength = column + nextParentTextLength +
                    parentText.split(".")[1].length
                if (line.txt.indexOf(".") == (column - 1) && totalLength <= 119) {
                    return
                }
                toBeLineBreak.add(
                    NormalLineBreak(
                        node,
                        lineBreak(context, line.start, indent + indent),
                        "'.' in the reference method call expression: $parentText."
                    )
                )
            }
        }
    }

    private fun breakClassDefine(
        context: FormatContext,
        node: ASTNode,
        line: Line
    ) {
        if (line.exceed) {
            when (context.scanningTimes) {
                SCAN_HIGH -> {
                    //第一遍扫描换行接口们
                    val implements = node.findChildByType(IMPLEMENTS_LIST)
                    implements?.children()?.forEach {
                        if (it is PsiJavaCodeReferenceElement) {
                            toBeLineBreak.add(
                                NormalLineBreak(
                                    it,
                                    lineBreak(context, line.start, indent + indent),
                                    "'implement' in the class define expression: ${node.text}."
                                )
                            )
                        }
                    }
                }
                SCAN_MIDDLE -> {
                    //第二遍扫描换行extends
                    val extends = node.findChildByType(EXTENDS_LIST)
                    extends?.children()?.forEach {
                        if (it is PsiJavaCodeReferenceElement) {
                            toBeLineBreak.add(
                                NormalLineBreak(
                                    it,
                                    lineBreak(context, line.start, indent + indent),
                                    "'extends' in the class define expression: ${node.text}."
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun breakStringLiteral(
        context: FormatContext,
        node: ASTNode, //STRING_LITERAL
        line: Line
    ) {

        val literal = node.treeParent
        if (literal != null && literal.elementType == LITERAL_EXPRESSION) {
            if (node.textLength > CutString.MAX_STRING_LEN) {
                toBeLineBreak.add(
                    CutString(literal, lineBreak(context, line.start, indent)))
            }
        }
    }

    /**
     * 在 [toBeBreak] 的位置插入一个换行的 [lineBreak]
     */
    private class NormalLineBreak(
        val toBeBreak: ASTNode,
        val lineBreak: ASTNode,
        val reason: String? = null
    ) : LineBreakAction {

        override fun run(context: FormatContext) {
            when {
                toBeBreak is PsiWhiteSpace -> {
                    toBeBreak.treeParent.replaceChild(toBeBreak, lineBreak)
                }
                toBeBreak.treePrev is PsiWhiteSpace -> {
                    toBeBreak.treeParent.replaceChild(toBeBreak.treePrev, lineBreak)
                }
                else -> {
                    toBeBreak.treeParent.addChild(lineBreak, toBeBreak)
                }
            }
        }

        override fun report(context: FormatContext) {
            context.report(
                "Add a line break${if (reason != null) ": $reason" else "."}",
                context.getCodeFragment(toBeBreak))
        }
    }

    /**
     * 把注释 [comment] 挪到 [parent] 的开头
     */
    private class MoveCommentToStart(
        val comment: ASTNode,
        val parent: ASTNode,
        val lineBreak: ASTNode
    ) : LineBreakAction {

        override fun run(context: FormatContext) {
            val whiteSpace = comment.treePrev
            if (whiteSpace is PsiWhiteSpace) {
                parent.removeChild(whiteSpace)
            }
            parent.removeChild(comment)
            val anchor = parent.children().firstOrNull()
            parent.addChild(comment, anchor)
            parent.addChild(lineBreak, anchor)
        }

        override fun report(context: FormatContext) {
            context.report(
                "Move comment to the start of the statement: ${comment.text}.",
                context.getCodeFragment(comment))
        }
    }

    /**
     * 如果注释 [comment] 过长，需要裁剪
     */
    private class CutEndOfLineComment(
        val comment: ASTNode
    ) : LineBreakAction {

        override fun run(context: FormatContext) {
            context.notifyTextChange()

            var startNode = comment
            var totalLength = comment.textLength
            val whiteSpace = comment.treePrev
            if (whiteSpace is PsiWhiteSpace) {
                startNode = whiteSpace
                totalLength += getIndent((whiteSpace as PsiWhiteSpace).text).length
            }

            if (totalLength >= maxLineLength) {
                val lineBreakText = lineBreak(startNode, "").text
                val whiteSpaceLen = lineBreakText.length

                fun checkAndCutComment(comment: String, addToTree: (ASTNode) -> Unit) {
                    if (comment.length + whiteSpaceLen >= maxLineLength) {
                        val half = comment.length / 2
                        checkAndCutComment(comment.substring(0, half), addToTree)
                        addToTree(PsiWhiteSpaceImpl(lineBreakText))
                        checkAndCutComment(comment.substring(half), addToTree)
                    } else {
                        val text =
                            if (comment.startsWith("//")) {
                                comment
                            } else {
                                "//$comment"
                            }
                        addToTree(PsiCoreCommentImpl(END_OF_LINE_COMMENT, text))
                    }
                }

                checkAndCutComment(comment.text) { child ->
                    comment.treeParent.addChild(child, comment)
                }
                comment.treeParent.removeChild(comment)
            }
        }

        override fun report(context: FormatContext) {
            context.report(
                "Cut the too long comment(//..): ${comment.text}.",
                context.getCodeFragment(comment))
        }
    }

    private class CutCStyleComment(
        val comment: ASTNode
    ) : LineBreakAction {
        override fun run(context: FormatContext) {
            val textLines = comment.text.split(lineBreak)
            val indentSize = lineBreak(comment, " ").textLength
            val newText = StringBuilder()
            textLines.forEach { text ->
                cutLine(text, indentSize) {
                    newText.append(it)
                }
            }
            val newComment = PsiCoreCommentImpl(C_STYLE_COMMENT, newText.toString())
            comment.treeParent.replaceChild(comment, newComment)
        }

        private fun cutLine(text: String, indentSize: Int, addToTree: (String) -> Unit) {
            if (text.length + indentSize > 100) {
                val half = text.length / 2
                cutLine(newLine(text.substring(0, half)), indentSize, addToTree)
                cutLine(newLine("\n" + " ".repeat(indentSize) + text.substring(half)),
                    indentSize, addToTree)
            } else {
                addToTree(text)
            }
        }

        private fun newLine(text: String): String {
            return if (text.startsWith(lineBreak)) text else lineBreak + text
        }

        override fun report(context: FormatContext) {
            context.report(
                "Cut the too long comment(/**/): ${comment.text}.",
                context.getCodeFragment(comment))
        }
    }

    private class CutString(
        val string: ASTNode, //LITERAL_EXPRESSION
        val lineBreakNode: ASTNode
    ) : LineBreakAction {

        companion object {
            const val MAX_STRING_LEN = 80
        }

        override fun run(context: FormatContext) {
            var polyadicExp = string.treeParent
            val anchorBefore: ASTNode?
            if (polyadicExp is PsiPolyadicExpression) {
                anchorBefore = string
            } else {
                polyadicExp = PsiPolyadicExpressionImpl()
                anchorBefore = null
                string.treeParent.replaceChild(string, polyadicExp)
            }

            cutStringIfNeed(string.text, lineBreakNode.text, true) { child ->
                polyadicExp.addChild(child, anchorBefore)
            }

            if (anchorBefore === string) {
                string.treeParent.removeChild(string)
            }
        }

        override fun report(context: FormatContext) {
            context.report(
                "Cut the too long String literal: ${string.text}.",
                context.getCodeFragment(string))
        }

        private fun cutStringIfNeed(
            node: String,
            lineBreak: String,
            mustCut: Boolean,
            addToTree: (ASTNode) -> Unit
        ) {
            if (mustCut || node.length > 80) {
                val half = node.length / 2
                cutStringIfNeed(
                    node.substring(0, half),
                    lineBreak,
                    false,
                    addToTree)
                addToTree(PsiWhiteSpaceImpl(" "))
                addToTree(PsiJavaTokenImpl(PLUS, "+"))
                addToTree(PsiWhiteSpaceImpl(lineBreak))
                cutStringIfNeed(
                    node.substring(half),
                    lineBreak,
                    false,
                    addToTree)
            } else {
                val literal = CompositeElement(LITERAL_EXPRESSION)
                addToTree(literal)
                var txtWithQuot: String = node
                if (!node.startsWith("\"")) {
                    txtWithQuot = "\"" + node
                }
                if (!node.endsWith("\"")) {
                    txtWithQuot = node + "\""
                }
                literal.addChild(PsiJavaTokenImpl(STRING_LITERAL, txtWithQuot))
            }
        }
    }

    override fun afterVisit(context: FormatContext) {
        super.afterVisit(context)
        toBeLineBreak.forEach { it.report(context) }
        toBeLineBreak.forEach { it.run(context) }

        if (lines.values.any { it.exceed } && context.scanningTimes < SCAN_E) {
            context.requestRepeatScan()
        }
    }

    private fun visitWholeFile(file: FileElement) {
        var lineStart = 0
        var lineEnd = 0
        val textLines = file.text.split(lineBreak)
        for ((index, line) in textLines.withIndex()) {
            val lineNum = index + 1
            lineEnd += line.length + 1
            lines[lineNum] = Line(
                lineNum, lineStart, lineEnd, line.length > maxLineLength, line)
            lineStart = lineEnd + 1
        }
    }
}