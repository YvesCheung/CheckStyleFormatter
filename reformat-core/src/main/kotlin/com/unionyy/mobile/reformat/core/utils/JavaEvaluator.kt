package com.unionyy.mobile.reformat.core.utils

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.jetbrains.kotlin.asJava.elements.KtLightModifierList
import org.jetbrains.kotlin.com.intellij.psi.PsiAnonymousClass
import org.jetbrains.kotlin.com.intellij.psi.PsiArrayType
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiClassType
import org.jetbrains.kotlin.com.intellij.psi.PsiCompiledElement
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiEllipsisType
import org.jetbrains.kotlin.com.intellij.psi.PsiMember
import org.jetbrains.kotlin.com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import org.jetbrains.kotlin.com.intellij.psi.PsiModifierListOwner
import org.jetbrains.kotlin.com.intellij.psi.PsiPrimitiveType
import org.jetbrains.kotlin.com.intellij.psi.PsiReference
import org.jetbrains.kotlin.com.intellij.psi.PsiType
import org.jetbrains.kotlin.com.intellij.psi.PsiTypeVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiWildcardType
import org.jetbrains.kotlin.com.intellij.psi.util.InheritanceUtil
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPsiFactory.CallableBuilder.Companion.CONSTRUCTOR_NAME

const val TYPE_OBJECT = "java.lang.Object"
const val TYPE_STRING = "java.lang.String"
const val TYPE_INT = "int"
const val TYPE_LONG = "long"
const val TYPE_CHAR = "char"
const val TYPE_FLOAT = "float"
const val TYPE_DOUBLE = "double"
const val TYPE_BOOLEAN = "boolean"
const val TYPE_SHORT = "short"
const val TYPE_BYTE = "byte"
const val TYPE_NULL = "null"
const val TYPE_INTEGER_WRAPPER = "java.lang.Integer"
const val TYPE_BOOLEAN_WRAPPER = "java.lang.Boolean"
const val TYPE_BYTE_WRAPPER = "java.lang.Byte"
const val TYPE_SHORT_WRAPPER = "java.lang.Short"
const val TYPE_LONG_WRAPPER = "java.lang.Long"
const val TYPE_DOUBLE_WRAPPER = "java.lang.Double"
const val TYPE_FLOAT_WRAPPER = "java.lang.Float"
const val TYPE_CHARACTER_WRAPPER = "java.lang.Character"

/**
 * @see com.android.tools.lint.client.api.JavaEvaluator
 */
open class JavaEvaluator {

    open fun extendsClass(cls: PsiClass?, className: String, strict: Boolean): Boolean {
        // TODO: This checks interfaces too. Let's find a cheaper method which only checks direct super classes!
        return InheritanceUtil.isInheritor(cls, strict, className)
    }

    open fun implementsInterface(
        cls: PsiClass,
        interfaceName: String,
        strict: Boolean
    ): Boolean {
        // TODO: This checks superclasses too. Let's find a cheaper method which only checks interfaces.
        return InheritanceUtil.isInheritor(cls, strict, interfaceName)
    }

    open fun isMemberInSubClassOf(
        member: PsiMember,
        className: String,
        strict: Boolean
    ): Boolean {
        val containingClass = member.containingClass
        return containingClass != null && extendsClass(containingClass, className, strict)
    }

    open fun isMemberInClass(
        member: PsiMember?,
        className: String
    ): Boolean {
        if (member == null) {
            return false
        }
        val containingClass = member.containingClass
        return containingClass != null && className == containingClass.qualifiedName
    }

    open fun getParameterCount(method: PsiMethod): Int {
        return method.parameterList.parametersCount
    }

    /**
     * Checks whether the class extends a super class or implements a given interface. Like calling
     * both [.extendsClass] and [ ][.implementsInterface].
     */
    open fun inheritsFrom(
        cls: PsiClass?,
        className: String,
        strict: Boolean
    ): Boolean {
        cls ?: return false
        return extendsClass(cls, className, strict) || implementsInterface(cls, className, strict)
    }

    /**
     * Returns true if the given method (which is typically looked up by resolving a method call) is
     * either a method in the exact given class, or if `allowInherit` is true, a method in a
     * class possibly extending the given class, and if the parameter types are the exact types
     * specified.
     *
     * @param method the method in question
     * @param className the class name the method should be defined in or inherit from (or
     * if null, allow any class)
     * @param allowInherit whether we allow checking for inheritance
     * @param argumentTypes the names of the types of the parameters
     * @return true if this method is defined in the given class and with the given parameters
     */
    open fun methodMatches(
        method: PsiMethod,
        className: String?,
        allowInherit: Boolean,
        vararg argumentTypes: String
    ): Boolean {
        if (className != null && allowInherit) {
            if (!isMemberInSubClassOf(method, className, false)) {
                return false
            }
        }

        return parametersMatch(method, *argumentTypes)
    }

    /**
     * Returns true if the given method's parameters are the exact types specified.
     *
     * @param method the method in question
     * @param argumentTypes the names of the types of the parameters
     * @return true if this method is defined in the given class and with the given parameters
     */
    open fun parametersMatch(
        method: PsiMethod,
        vararg argumentTypes: String
    ): Boolean {
        val parameterList = method.parameterList
        if (parameterList.parametersCount != argumentTypes.size) {
            return false
        }
        val parameters = parameterList.parameters
        for (i in parameters.indices) {
            val type = parameters[i].type
            if (type.canonicalText != argumentTypes[i]) {
                return false
            }
        }

        return true
    }

    /** Returns true if the given type matches the given fully qualified type name  */
    open fun parameterHasType(
        method: PsiMethod?,
        parameterIndex: Int,
        typeName: String
    ): Boolean {
        if (method == null) {
            return false
        }
        val parameterList = method.parameterList
        return parameterList.parametersCount > parameterIndex && typeMatches(
            parameterList.parameters[parameterIndex].type,
            typeName
        )
    }

    /** Returns true if the given type matches the given fully qualified type name  */
    open fun typeMatches(
        type: PsiType?,
        typeName: String
    ): Boolean {
        return type != null && type.canonicalText == typeName
    }

    open fun resolve(element: PsiElement): PsiElement? {
        if (element is PsiReference) {
            return (element as PsiReference).resolve()
        } else if (element is PsiMethodCallExpression) {
            val resolved = element.resolveMethod()
            if (resolved != null) {
                return resolved
            }
        }
        return null
    }

    open fun isPublic(owner: PsiModifierListOwner?): Boolean {
        if (owner != null) {
            val modifierList = owner.modifierList
            return modifierList != null && modifierList.hasModifierProperty(PsiModifier.PUBLIC)
        }
        return false
    }

    open fun isProtected(owner: PsiModifierListOwner?): Boolean {
        if (owner != null) {
            val modifierList = owner.modifierList
            return modifierList != null && modifierList.hasModifierProperty(PsiModifier.PROTECTED)
        }
        return false
    }

    open fun isStatic(owner: PsiModifierListOwner?): Boolean {
        if (owner != null) {
            val modifierList = owner.modifierList
            return modifierList != null && modifierList.hasModifierProperty(PsiModifier.STATIC)
        }
        return false
    }

    open fun isPrivate(owner: PsiModifierListOwner?): Boolean {
        if (owner != null) {
            val modifierList = owner.modifierList
            return modifierList != null && modifierList.hasModifierProperty(PsiModifier.PRIVATE)
        }
        return false
    }

    open fun isAbstract(owner: PsiModifierListOwner?): Boolean {
        if (owner != null) {
            val modifierList = owner.modifierList
            return modifierList != null && modifierList.hasModifierProperty(PsiModifier.ABSTRACT)
        }
        return false
    }

    @Suppress("unused")
    open fun isFinal(owner: PsiModifierListOwner?): Boolean {
        if (owner != null) {
            val modifierList = owner.modifierList
            return modifierList != null && modifierList.hasModifierProperty(PsiModifier.FINAL)
        }
        return false
    }

    open fun isInternal(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.INTERNAL_KEYWORD)
    }

    open fun isSealed(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.SEALED_KEYWORD)
    }

    open fun isData(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.DATA_KEYWORD)
    }

    open fun isLateInit(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.LATEINIT_KEYWORD)
    }

    open fun isInline(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.INLINE_KEYWORD)
    }

    open fun isOperator(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.OPERATOR_KEYWORD)
    }

    open fun isInfix(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.INFIX_KEYWORD)
    }

    open fun isSuspend(owner: PsiModifierListOwner?): Boolean {
        return hasModifier(owner, KtTokens.SUSPEND_KEYWORD)
    }

    open fun hasModifier(owner: PsiModifierListOwner?, keyword: KtModifierKeywordToken): Boolean {
        if (owner != null) {
            val modifierList = owner.modifierList
            if (modifierList is KtLightModifierList<*>) {
                val ktModifierList = modifierList.kotlinOrigin
                if (ktModifierList != null) {
                    return ktModifierList.hasModifier(keyword)
                }
            }
        }
        return false
    }

    open fun getSuperMethod(method: PsiMethod?): PsiMethod? {
        if (method == null) {
            return null
        }
        val superMethods = method.findSuperMethods()
        if (superMethods.size > 1) {
            // Prefer non-compiled concrete methods
            for (m in superMethods) {
                if (m !is PsiCompiledElement && m.containingClass?.isInterface == false) {
                    return m
                }
            }
            for (m in superMethods) {
                if (m.containingClass?.isInterface == false) {
                    return m
                }
            }
            for (m in superMethods) {
                if (m !is PsiCompiledElement) {
                    return m
                }
            }
        }
        return if (superMethods.isNotEmpty()) {
            superMethods[0]
        } else null
    }

    open fun getQualifiedName(psiClass: PsiClass): String? {
        var qualifiedName = psiClass.qualifiedName
        if (qualifiedName == null) {
            qualifiedName = psiClass.name
            if (qualifiedName == null) {
                assert(psiClass is PsiAnonymousClass)

                return getQualifiedName(psiClass.containingClass!!)
            }
        }
        return qualifiedName
    }

    open fun getQualifiedName(psiClassType: PsiClassType): String? {
        return psiClassType.canonicalText
    }

    /**
     * Computes a simplified version of the internal JVM description of the given method. This is in
     * the same format as the ASM desc fields for methods with an exception that the dot ('.')
     * character is used instead of slash ('/') and dollar sign ('$') characters. For example,
     * a method named "foo" that takes an int and a String and returns a void will have description
     * `foo(ILjava.lang.String;):V`.
     *
     * @param method the method to look up the description for
     * @param includeName whether the name should be included
     * @param includeReturn whether the return type should be included
     * @return a simplified version of the internal JVM description for the method
     */
    open fun getMethodDescription(
        method: PsiMethod,
        includeName: Boolean,
        includeReturn: Boolean
    ): String? {
        assert(!includeName) // not yet tested
        assert(!includeReturn) // not yet tested

        val signature = StringBuilder()

        if (includeName) {
            if (method.isConstructor) {
                val declaringClass = method.containingClass
                if (declaringClass != null) {
                    val outerClass = declaringClass.containingClass
                    if (outerClass != null) {
                        // declaring class is an inner class
                        if (!declaringClass.hasModifierProperty(PsiModifier.STATIC)) {
                            if (!appendJvmEquivalentTypeName(signature, outerClass)) {
                                return null
                            }
                        }
                    }
                }
                signature.append(CONSTRUCTOR_NAME)
            } else {
                signature.append(method.name)
            }
        }

        signature.append('(')

        for (psiParameter in method.parameterList.parameters) {
            if (!appendJvmEquivalentSignature(signature, psiParameter.type)) {
                return null
            }
        }
        signature.append(')')
        if (includeReturn) {
            if (!method.isConstructor) {
                if (!appendJvmEquivalentSignature(signature, method.returnType)) {
                    return null
                }
            } else {
                signature.append('V')
            }
        }
        return signature.toString()
    }

    /**
     * Constructs a simplified version of the internal JVM description of the given method. This is
     * in the same format as {@link #getMethodDescription} above, the difference being we don't have
     * the actual PSI for the method type, we just construct the signature from the [method] name,
     * the list of [argumentTypes] and optionally include the [returnType].
     */
    open fun constructMethodDescription(
        method: String,
        includeName: Boolean = false,
        argumentTypes: Array<PsiType>,
        returnType: PsiType? = null,
        includeReturn: Boolean = false
    ): String? = buildString {
        if (includeName) {
            append(method)
        }
        append('(')
        for (argumentType in argumentTypes) {
            if (!appendJvmEquivalentSignature(this, argumentType)) {
                return null
            }
        }
        append(')')
        if (includeReturn) {
            if (!appendJvmEquivalentSignature(this, returnType)) {
                return null
            }
        }
    }

    /**
     * The JVM equivalent type name differs from the real JVM name by using dot ('.') instead of
     * slash ('/') and dollar sign ('$') characters.
     */
    private fun appendJvmEquivalentTypeName(
        signature: StringBuilder,
        outerClass: PsiClass
    ): Boolean {
        val className = getQualifiedName(outerClass) ?: return false
        signature.append('L').append(className).append(';')
        return true
    }

    /**
     * The JVM equivalent signature differs from the real JVM signature by using dot ('.') instead
     * of slash ('/') and dollar sign ('$') characters.
     */
    private fun appendJvmEquivalentSignature(
        buffer: StringBuilder,
        type: PsiType?
    ): Boolean {
        if (type == null) {
            return false
        }

        val psiType = erasure(type)

        if (psiType is PsiArrayType) {
            buffer.append('[')
            appendJvmEquivalentSignature(buffer, psiType.componentType)
        } else if (psiType is PsiClassType) {
            val resolved = psiType.resolve() ?: return false
            if (!appendJvmEquivalentTypeName(buffer, resolved)) {
                return false
            }
        } else if (psiType is PsiPrimitiveType) {
            buffer.append(getPrimitiveSignature(psiType.canonicalText))
        } else {
            return false
        }
        return true
    }

    open fun areSignaturesEqual(method1: PsiMethod, method2: PsiMethod): Boolean {
        val parameterList1 = method1.parameterList
        val parameterList2 = method2.parameterList
        if (parameterList1.parametersCount != parameterList2.parametersCount) {
            return false
        }

        val parameters1 = parameterList1.parameters
        val parameters2 = parameterList2.parameters

        var i = 0
        val n = parameters1.size
        while (i < n) {
            val parameter1 = parameters1[i]
            val parameter2 = parameters2[i]
            var type1: PsiType? = parameter1.type
            var type2: PsiType? = parameter2.type
            if (type1 != type2) {
                type1 = erasure(parameter1.type)
                type2 = erasure(parameter2.type)
                if (type1 != type2) {
                    return false
                }
            }
            i++
        }

        return true
    }

    open fun erasure(type: PsiType?): PsiType? {
        return type?.accept(object : PsiTypeVisitor<PsiType>() {
            override fun visitType(type: PsiType?): PsiType? {
                return type
            }

            override fun visitClassType(classType: PsiClassType): PsiType? {
                return classType.rawType()
            }

            override fun visitWildcardType(wildcardType: PsiWildcardType): PsiType? {
                return wildcardType
            }

            override fun visitPrimitiveType(primitiveType: PsiPrimitiveType): PsiType? {
                return primitiveType
            }

            override fun visitEllipsisType(ellipsisType: PsiEllipsisType): PsiType? {
                val componentType = ellipsisType.componentType
                val newComponentType = componentType.accept(this)
                return if (newComponentType === componentType) ellipsisType else newComponentType?.createArrayType()
            }

            override fun visitArrayType(arrayType: PsiArrayType): PsiType? {
                val componentType = arrayType.componentType
                val newComponentType = componentType.accept(this)
                return if (newComponentType === componentType) arrayType else newComponentType?.createArrayType()
            }
        })
    }

    companion object {
        fun getPrimitiveSignature(typeName: String): String? = when (typeName) {
            "boolean" -> "Z"
            "byte" -> "B"
            "char" -> "C"
            "short" -> "S"
            "int" -> "I"
            "long" -> "J"
            "float" -> "F"
            "double" -> "D"
            "void" -> "V"
            else -> null
        }
    }
}
