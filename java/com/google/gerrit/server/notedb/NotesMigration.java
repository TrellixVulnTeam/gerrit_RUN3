begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
operator|.
name|NoteDbTable
operator|.
name|CHANGES
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|server
operator|.
name|notedb
operator|.
name|NoteDbChangeState
operator|.
name|PrimaryStorage
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|AbstractModule
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
name|atomic
operator|.
name|AtomicReference
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

begin_comment
comment|/**  * Current low-level settings of the NoteDb migration for changes.  *  *<p>This class only describes the migration state of the {@link  * com.google.gerrit.reviewdb.client.Change Change} entity group, since it is possible for a given  * site to be in different states of the Change NoteDb migration process while staying at the same  * ReviewDb schema version. It does<em>not</em> describe the migration state of non-Change tables;  * those are automatically migrated using the ReviewDb schema migration process, so the NoteDb  * migration state at a given ReviewDb schema cannot vary.  *  *<p>In many places, core Gerrit code should not directly care about the NoteDb migration state,  * and should prefer high-level APIs like {@link com.google.gerrit.server.ApprovalsUtil  * ApprovalsUtil} that don't require callers to inspect the migration state. The  *<em>implementation</em> of those utilities does care about the state, and should query the {@code  * NotesMigration} for the properties of the migration, for example, {@link #changePrimaryStorage()  * where new changes should be stored}.  *  *<p>Core Gerrit code is mostly interested in one facet of the migration at a time (reading or  * writing, say), but not all combinations of return values are supported or even make sense.  *  *<p>This class controls the state of the migration according to options in {@code gerrit.config}.  * In general, any changes to these options should only be made by adventurous administrators, who  * know what they're doing, on non-production data, for the purposes of testing the NoteDb  * implementation.  *  *<p><strong>Note:</strong> Callers should not assume the values returned by {@code  * NotesMigration}'s methods will not change in a running server.  */
end_comment

begin_class
DECL|class|NotesMigration
specifier|public
specifier|abstract
class|class
name|NotesMigration
block|{
DECL|field|SECTION_NOTE_DB
specifier|public
specifier|static
specifier|final
name|String
name|SECTION_NOTE_DB
init|=
literal|"noteDb"
decl_stmt|;
DECL|field|READ
specifier|public
specifier|static
specifier|final
name|String
name|READ
init|=
literal|"read"
decl_stmt|;
DECL|field|WRITE
specifier|public
specifier|static
specifier|final
name|String
name|WRITE
init|=
literal|"write"
decl_stmt|;
DECL|field|DISABLE_REVIEW_DB
specifier|public
specifier|static
specifier|final
name|String
name|DISABLE_REVIEW_DB
init|=
literal|"disableReviewDb"
decl_stmt|;
DECL|field|PRIMARY_STORAGE
specifier|public
specifier|static
specifier|final
name|String
name|PRIMARY_STORAGE
init|=
literal|"primaryStorage"
decl_stmt|;
DECL|field|SEQUENCE
specifier|public
specifier|static
specifier|final
name|String
name|SEQUENCE
init|=
literal|"sequence"
decl_stmt|;
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|MutableNotesMigration
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|NotesMigration
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|MutableNotesMigration
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AutoValue
DECL|class|Snapshot
specifier|abstract
specifier|static
class|class
name|Snapshot
block|{
DECL|method|builder ()
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
comment|// Default values are defined as what we would read from an empty config.
return|return
name|create
argument_list|(
operator|new
name|Config
argument_list|()
argument_list|)
operator|.
name|toBuilder
argument_list|()
return|;
block|}
DECL|method|create (Config cfg)
specifier|static
name|Snapshot
name|create
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_NotesMigration_Snapshot
operator|.
name|Builder
argument_list|()
operator|.
name|setWriteChanges
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|WRITE
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setReadChanges
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|READ
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setReadChangeSequence
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|SEQUENCE
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setChangePrimaryStorage
argument_list|(
name|cfg
operator|.
name|getEnum
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|PRIMARY_STORAGE
argument_list|,
name|PrimaryStorage
operator|.
name|REVIEW_DB
argument_list|)
argument_list|)
operator|.
name|setDisableChangeReviewDb
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|DISABLE_REVIEW_DB
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setFailOnLoadForTest
argument_list|(
literal|false
argument_list|)
comment|// Only set in tests, can't be set via config.
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|writeChanges ()
specifier|abstract
name|boolean
name|writeChanges
parameter_list|()
function_decl|;
DECL|method|readChanges ()
specifier|abstract
name|boolean
name|readChanges
parameter_list|()
function_decl|;
DECL|method|readChangeSequence ()
specifier|abstract
name|boolean
name|readChangeSequence
parameter_list|()
function_decl|;
DECL|method|changePrimaryStorage ()
specifier|abstract
name|PrimaryStorage
name|changePrimaryStorage
parameter_list|()
function_decl|;
DECL|method|disableChangeReviewDb ()
specifier|abstract
name|boolean
name|disableChangeReviewDb
parameter_list|()
function_decl|;
DECL|method|failOnLoadForTest ()
specifier|abstract
name|boolean
name|failOnLoadForTest
parameter_list|()
function_decl|;
DECL|method|toBuilder ()
specifier|abstract
name|Builder
name|toBuilder
parameter_list|()
function_decl|;
DECL|method|setConfigValues (Config cfg)
name|void
name|setConfigValues
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
name|cfg
operator|.
name|setBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|WRITE
argument_list|,
name|writeChanges
argument_list|()
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|READ
argument_list|,
name|readChanges
argument_list|()
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|SEQUENCE
argument_list|,
name|readChangeSequence
argument_list|()
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setEnum
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|PRIMARY_STORAGE
argument_list|,
name|changePrimaryStorage
argument_list|()
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
name|SECTION_NOTE_DB
argument_list|,
name|CHANGES
operator|.
name|key
argument_list|()
argument_list|,
name|DISABLE_REVIEW_DB
argument_list|,
name|disableChangeReviewDb
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AutoValue
operator|.
name|Builder
DECL|class|Builder
specifier|abstract
specifier|static
class|class
name|Builder
block|{
DECL|method|setWriteChanges (boolean writeChanges)
specifier|abstract
name|Builder
name|setWriteChanges
parameter_list|(
name|boolean
name|writeChanges
parameter_list|)
function_decl|;
DECL|method|setReadChanges (boolean readChanges)
specifier|abstract
name|Builder
name|setReadChanges
parameter_list|(
name|boolean
name|readChanges
parameter_list|)
function_decl|;
DECL|method|setReadChangeSequence (boolean readChangeSequence)
specifier|abstract
name|Builder
name|setReadChangeSequence
parameter_list|(
name|boolean
name|readChangeSequence
parameter_list|)
function_decl|;
DECL|method|setChangePrimaryStorage (PrimaryStorage changePrimaryStorage)
specifier|abstract
name|Builder
name|setChangePrimaryStorage
parameter_list|(
name|PrimaryStorage
name|changePrimaryStorage
parameter_list|)
function_decl|;
DECL|method|setDisableChangeReviewDb (boolean disableChangeReviewDb)
specifier|abstract
name|Builder
name|setDisableChangeReviewDb
parameter_list|(
name|boolean
name|disableChangeReviewDb
parameter_list|)
function_decl|;
DECL|method|setFailOnLoadForTest (boolean failOnLoadForTest)
specifier|abstract
name|Builder
name|setFailOnLoadForTest
parameter_list|(
name|boolean
name|failOnLoadForTest
parameter_list|)
function_decl|;
DECL|method|autoBuild ()
specifier|abstract
name|Snapshot
name|autoBuild
parameter_list|()
function_decl|;
DECL|method|build ()
name|Snapshot
name|build
parameter_list|()
block|{
name|Snapshot
name|s
init|=
name|autoBuild
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
operator|!
operator|(
name|s
operator|.
name|disableChangeReviewDb
argument_list|()
operator|&&
name|s
operator|.
name|changePrimaryStorage
argument_list|()
operator|!=
name|PrimaryStorage
operator|.
name|NOTE_DB
operator|)
argument_list|,
literal|"cannot disable ReviewDb for changes if default change primary storage is ReviewDb"
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
block|}
block|}
DECL|field|snapshot
specifier|protected
specifier|final
name|AtomicReference
argument_list|<
name|Snapshot
argument_list|>
name|snapshot
decl_stmt|;
comment|/**    * Read changes from NoteDb.    *    *<p>Change data is read from NoteDb refs, but ReviewDb is still the source of truth. If the    * loader determines NoteDb is out of date, the change data in NoteDb will be transparently    * rebuilt. This means that some code paths that look read-only may in fact attempt to write.    *    *<p>If true and {@code writeChanges() = false}, changes can still be read from NoteDb, but any    * attempts to write will generate an error.    */
DECL|method|readChanges ()
specifier|public
specifier|final
name|boolean
name|readChanges
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|readChanges
argument_list|()
return|;
block|}
comment|/**    * Write changes to NoteDb.    *    *<p>This method is awkwardly named because you should be using either {@link    * #commitChangeWrites()} or {@link #failChangeWrites()} instead.    *    *<p>Updates to change data are written to NoteDb refs, but ReviewDb is still the source of    * truth. Change data will not be written unless the NoteDb refs are already up to date, and the    * write path will attempt to rebuild the change if not.    *    *<p>If false, the behavior when attempting to write depends on {@code readChanges()}. If {@code    * readChanges() = false}, writes to NoteDb are simply ignored; if {@code true}, any attempts to    * write will generate an error.    */
DECL|method|rawWriteChangesSetting ()
specifier|public
specifier|final
name|boolean
name|rawWriteChangesSetting
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|writeChanges
argument_list|()
return|;
block|}
comment|/**    * Read sequential change ID numbers from NoteDb.    *    *<p>If true, change IDs are read from {@code refs/sequences/changes} in All-Projects. If false,    * change IDs are read from ReviewDb's native sequences.    */
DECL|method|readChangeSequence ()
specifier|public
specifier|final
name|boolean
name|readChangeSequence
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|readChangeSequence
argument_list|()
return|;
block|}
comment|/** @return default primary storage for new changes. */
DECL|method|changePrimaryStorage ()
specifier|public
specifier|final
name|PrimaryStorage
name|changePrimaryStorage
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|changePrimaryStorage
argument_list|()
return|;
block|}
comment|/**    * Disable ReviewDb access for changes.    *    *<p>When set, ReviewDb operations involving the Changes table become no-ops. Lookups return no    * results; updates do nothing, as does opening, committing, or rolling back a transaction on the    * Changes table.    */
DECL|method|disableChangeReviewDb ()
specifier|public
specifier|final
name|boolean
name|disableChangeReviewDb
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|disableChangeReviewDb
argument_list|()
return|;
block|}
comment|/**    * Whether to fail when reading any data from NoteDb.    *    *<p>Used in conjunction with {@link #readChanges()} for tests.    */
DECL|method|failOnLoadForTest ()
specifier|public
name|boolean
name|failOnLoadForTest
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|failOnLoadForTest
argument_list|()
return|;
block|}
DECL|method|commitChangeWrites ()
specifier|public
specifier|final
name|boolean
name|commitChangeWrites
parameter_list|()
block|{
comment|// It may seem odd that readChanges() without writeChanges() means we should
comment|// attempt to commit writes. However, this method is used by callers to know
comment|// whether or not they should short-circuit and skip attempting to read or
comment|// write NoteDb refs.
comment|//
comment|// It is possible for commitChangeWrites() to return true and
comment|// failChangeWrites() to also return true, causing an error later in the
comment|// same codepath. This specific condition is used by the auto-rebuilding
comment|// path to rebuild a change and stage the results, but not commit them due
comment|// to failChangeWrites().
return|return
name|rawWriteChangesSetting
argument_list|()
operator|||
name|readChanges
argument_list|()
return|;
block|}
DECL|method|failChangeWrites ()
specifier|public
specifier|final
name|boolean
name|failChangeWrites
parameter_list|()
block|{
return|return
operator|!
name|rawWriteChangesSetting
argument_list|()
operator|&&
name|readChanges
argument_list|()
return|;
block|}
DECL|method|setConfigValues (Config cfg)
specifier|public
specifier|final
name|void
name|setConfigValues
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|setConfigValues
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|NotesMigration
operator|&&
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NotesMigration
operator|)
name|o
operator|)
operator|.
name|snapshot
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|NotesMigration (Snapshot snapshot)
specifier|protected
name|NotesMigration
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|snapshot
operator|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
block|}
DECL|method|snapshot ()
specifier|final
name|Snapshot
name|snapshot
parameter_list|()
block|{
return|return
name|snapshot
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

