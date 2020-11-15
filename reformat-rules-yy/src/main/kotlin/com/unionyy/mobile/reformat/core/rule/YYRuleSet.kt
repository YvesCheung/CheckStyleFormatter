package com.unionyy.mobile.reformat.core.rule

import com.unionyy.mobile.reformat.core.FormatRule

/**
 * @author YvesCheung
 * 2020/11/15
 */
val YYRuleSet: List<FormatRule> = listOf(
    ContinuousCodeBlock(),
    ArrayBracket(),
    ModifierRule(),
    ContinuousExpression(),
    LineBreaker(),
    SpaceOperation(),
    EmptyStatement(),
    EmptyBlockRule(),
    AddSwitchDefaultCase()
)