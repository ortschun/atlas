/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.atlas.core.index

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.netflix.atlas.core.model.Tag
import com.netflix.atlas.core.model.TagKey
import com.netflix.atlas.core.model.TaggedItem


/**
 * Caches stdext query results.
 */
class CachingTagIndex[T <: TaggedItem](delegate: TagIndex[T]) extends TagIndex[T] {

  private val findTagsCache = newCache[List[Tag]](delegate.findTags)
  private val findKeysCache = newCache[List[TagKey]](delegate.findKeys)
  private val findValuesCache = newCache[List[String]](delegate.findValues)
  private val findItemsCache = newCache[List[T]](delegate.findItems)

  private def newCache[R <: AnyRef](f: TagQuery => R): LoadingCache[TagQuery, R] = {
    val loader = new CacheLoader[TagQuery, R] {
      def load(query: TagQuery): R = f(query)
    }
    CacheBuilder.newBuilder.maximumSize(1000).build[TagQuery, R](loader)
  }

  def findTags(query: TagQuery): List[Tag] = {
    findTagsCache.get(query)
  }

  def findKeys(query: TagQuery): List[TagKey] = {
    findKeysCache.get(query)
  }

  def findValues(query: TagQuery): List[String] = {
    findValuesCache.get(query)
  }

  def findItems(query: TagQuery): List[T] = {
    findItemsCache.get(query)
  }

  def size: Int = delegate.size
}
