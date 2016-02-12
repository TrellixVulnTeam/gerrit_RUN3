begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Licensed under the Apache License, Version 2.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License.
end_comment

begin_comment
comment|// You may obtain a copy of the License at
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// limitations under the License.
end_comment

begin_package
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|DAYS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|HOURS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MINUTES
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|client
operator|.
name|Theme
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
DECL|class|ConfigUtilTest
specifier|public
class|class
name|ConfigUtilTest
block|{
DECL|field|SECT
specifier|private
specifier|static
specifier|final
name|String
name|SECT
init|=
literal|"foo"
decl_stmt|;
DECL|field|SUB
specifier|private
specifier|static
specifier|final
name|String
name|SUB
init|=
literal|"bar"
decl_stmt|;
DECL|class|SectionInfo
specifier|static
class|class
name|SectionInfo
block|{
DECL|field|CONSTANT
specifier|public
specifier|static
specifier|final
name|String
name|CONSTANT
init|=
literal|"42"
decl_stmt|;
DECL|field|missing
specifier|public
specifier|transient
name|String
name|missing
decl_stmt|;
DECL|field|i
specifier|public
name|int
name|i
decl_stmt|;
DECL|field|ii
specifier|public
name|Integer
name|ii
decl_stmt|;
DECL|field|id
specifier|public
name|Integer
name|id
decl_stmt|;
DECL|field|l
specifier|public
name|long
name|l
decl_stmt|;
DECL|field|ll
specifier|public
name|Long
name|ll
decl_stmt|;
DECL|field|ld
specifier|public
name|Long
name|ld
decl_stmt|;
DECL|field|b
specifier|public
name|boolean
name|b
decl_stmt|;
DECL|field|bb
specifier|public
name|Boolean
name|bb
decl_stmt|;
DECL|field|bd
specifier|public
name|Boolean
name|bd
decl_stmt|;
DECL|field|s
specifier|public
name|String
name|s
decl_stmt|;
DECL|field|sd
specifier|public
name|String
name|sd
decl_stmt|;
DECL|field|nd
specifier|public
name|String
name|nd
decl_stmt|;
DECL|field|t
specifier|public
name|Theme
name|t
decl_stmt|;
DECL|field|td
specifier|public
name|Theme
name|td
decl_stmt|;
DECL|field|list
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|list
decl_stmt|;
DECL|field|map
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
DECL|method|defaults ()
specifier|static
name|SectionInfo
name|defaults
parameter_list|()
block|{
name|SectionInfo
name|i
init|=
operator|new
name|SectionInfo
argument_list|()
decl_stmt|;
name|i
operator|.
name|i
operator|=
literal|1
expr_stmt|;
name|i
operator|.
name|ii
operator|=
literal|2
expr_stmt|;
name|i
operator|.
name|id
operator|=
literal|3
expr_stmt|;
name|i
operator|.
name|l
operator|=
literal|4L
expr_stmt|;
name|i
operator|.
name|ll
operator|=
literal|5L
expr_stmt|;
name|i
operator|.
name|ld
operator|=
literal|6L
expr_stmt|;
name|i
operator|.
name|b
operator|=
literal|true
expr_stmt|;
name|i
operator|.
name|bb
operator|=
literal|false
expr_stmt|;
name|i
operator|.
name|bd
operator|=
literal|true
expr_stmt|;
name|i
operator|.
name|s
operator|=
literal|"foo"
expr_stmt|;
name|i
operator|.
name|sd
operator|=
literal|"bar"
expr_stmt|;
comment|// i.nd = null; // Don't need to explicitly set it; it's null by default
name|i
operator|.
name|t
operator|=
name|Theme
operator|.
name|DEFAULT
expr_stmt|;
name|i
operator|.
name|td
operator|=
name|Theme
operator|.
name|DEFAULT
expr_stmt|;
return|return
name|i
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStoreLoadSection ()
specifier|public
name|void
name|testStoreLoadSection
parameter_list|()
throws|throws
name|Exception
block|{
name|SectionInfo
name|d
init|=
name|SectionInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|SectionInfo
name|in
init|=
operator|new
name|SectionInfo
argument_list|()
decl_stmt|;
name|in
operator|.
name|missing
operator|=
literal|"42"
expr_stmt|;
name|in
operator|.
name|i
operator|=
literal|1
expr_stmt|;
name|in
operator|.
name|ii
operator|=
literal|43
expr_stmt|;
name|in
operator|.
name|l
operator|=
literal|4L
expr_stmt|;
name|in
operator|.
name|ll
operator|=
operator|-
literal|43L
expr_stmt|;
name|in
operator|.
name|b
operator|=
literal|false
expr_stmt|;
name|in
operator|.
name|bb
operator|=
literal|true
expr_stmt|;
name|in
operator|.
name|bd
operator|=
literal|false
expr_stmt|;
name|in
operator|.
name|s
operator|=
literal|"baz"
expr_stmt|;
name|in
operator|.
name|t
operator|=
name|Theme
operator|.
name|MIDNIGHT
expr_stmt|;
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|storeSection
argument_list|(
name|cfg
argument_list|,
name|SECT
argument_list|,
name|SUB
argument_list|,
name|in
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"CONSTANT"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"missing"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"b"
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|b
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"bb"
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|bb
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"i"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"ii"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|ii
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getLong
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"l"
argument_list|,
literal|0L
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getLong
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"ll"
argument_list|,
literal|0L
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|ll
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"s"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|s
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"sd"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
name|SECT
argument_list|,
name|SUB
argument_list|,
literal|"nd"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|SectionInfo
name|out
init|=
operator|new
name|SectionInfo
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|loadSection
argument_list|(
name|cfg
argument_list|,
name|SECT
argument_list|,
name|SUB
argument_list|,
name|out
argument_list|,
name|d
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|i
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|i
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|ii
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|ii
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d
operator|.
name|id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|l
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|l
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|ll
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|ll
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|ld
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d
operator|.
name|ld
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|b
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|b
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|bb
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|bb
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|bd
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|s
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|s
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|sd
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d
operator|.
name|sd
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|nd
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|t
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|t
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|out
operator|.
name|td
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d
operator|.
name|td
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mergeSection ()
specifier|public
name|void
name|mergeSection
parameter_list|()
throws|throws
name|Exception
block|{
name|SectionInfo
name|d
init|=
name|SectionInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|storeSection
argument_list|(
name|cfg
argument_list|,
name|SECT
argument_list|,
name|SUB
argument_list|,
name|d
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|SectionInfo
name|in
init|=
operator|new
name|SectionInfo
argument_list|()
decl_stmt|;
name|in
operator|.
name|i
operator|=
literal|42
expr_stmt|;
name|SectionInfo
name|out
init|=
operator|new
name|SectionInfo
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|loadSection
argument_list|(
name|cfg
argument_list|,
name|SECT
argument_list|,
name|SUB
argument_list|,
name|out
argument_list|,
name|d
argument_list|,
name|in
argument_list|)
expr_stmt|;
comment|// Check original values preserved
name|assertThat
argument_list|(
name|out
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d
operator|.
name|id
argument_list|)
expr_stmt|;
comment|// Check merged values
name|assertThat
argument_list|(
name|out
operator|.
name|i
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|in
operator|.
name|i
argument_list|)
expr_stmt|;
comment|// Check that boolean attribute not nullified
name|assertThat
argument_list|(
name|out
operator|.
name|bb
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimeUnit ()
specifier|public
name|void
name|testTimeUnit
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|0
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|2
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2ms"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|200
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"200 milliseconds"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|0
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"0s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|2
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|231
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"231sec"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|1
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1second"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|300
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"300 seconds"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|2
argument_list|,
name|MINUTES
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2m"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|2
argument_list|,
name|MINUTES
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2min"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|1
argument_list|,
name|MINUTES
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1 minute"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|10
argument_list|,
name|MINUTES
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"10 minutes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|5
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"5h"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|5
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"5hr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|1
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1hour"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|48
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"48hours"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|5
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"5 h"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|5
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"5 hr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|1
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1 hour"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|48
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"48 hours"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|48
argument_list|,
name|HOURS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"48 \t \r hours"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|4
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"4d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|1
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1day"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|14
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"14days"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|7
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1w"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|7
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1week"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|14
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2w"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|14
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2weeks"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|30
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1mon"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|30
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1month"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|60
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2mon"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|60
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2months"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|365
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|365
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"1year"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ms
argument_list|(
literal|365
operator|*
literal|2
argument_list|,
name|DAYS
argument_list|)
argument_list|,
name|parse
argument_list|(
literal|"2years"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ms (int cnt, TimeUnit unit)
specifier|private
specifier|static
name|long
name|ms
parameter_list|(
name|int
name|cnt
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|cnt
argument_list|,
name|unit
argument_list|)
return|;
block|}
DECL|method|parse (String string)
specifier|private
specifier|static
name|long
name|parse
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|string
argument_list|,
literal|1
argument_list|,
name|MILLISECONDS
argument_list|)
return|;
block|}
block|}
end_class

end_unit

