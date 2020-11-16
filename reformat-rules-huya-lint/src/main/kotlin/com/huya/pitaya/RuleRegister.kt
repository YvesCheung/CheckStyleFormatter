package com.huya.pitaya

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/**
 * @author YvesCheung
 * 2020/11/16
 */
@Suppress("UnstableApiUsage")
class RuleRegister : IssueRegistry() {

    override val issues: List<Issue> =
        listOf(HyExDetector.ISSUE_COLLECTION_EX)

    override val api: Int = CURRENT_API

    override val minApi: Int = 1
}