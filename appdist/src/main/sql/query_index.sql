-- Indexes to support @Query
--

-- *********************************************************************
-- AccountAccess
--    covers:             byPreferredEmail, suggestByPreferredEmail
CREATE INDEX accounts_byPreferredEmail
ON accounts (preferred_email);

--    covers:             bySshUserName
CREATE INDEX accounts_bySshUserName
ON accounts (ssh_user_name);

--    covers:             suggestByFullName
CREATE INDEX accounts_byFullName
ON accounts (full_name);


-- *********************************************************************
-- AccountAgreementAccess
--    @PrimaryKey covers: byAccount


-- *********************************************************************
-- AccountExternalIdAccess
--    @PrimaryKey covers: byAccount
--    covers:             byExternal
CREATE INDEX account_external_ids_byExt
ON account_external_ids (external_id);

--    covers:             byEmailAddress, suggestByEmailAddress
CREATE INDEX account_external_ids_byEmail
ON account_external_ids (email_address);


-- *********************************************************************
-- AccountGroupAccess
--    @SecondaryKey("name") covers:  all, suggestByName
CREATE INDEX account_groups_ownedByGroup
ON account_groups (owner_group_id);


-- *********************************************************************
-- AccountGroupMemberAccess
--    @PrimaryKey covers: byAccount
CREATE INDEX account_group_members_byGroup
ON account_group_members (group_id);


-- *********************************************************************
-- AccountProjectWatchAccess
--    @PrimaryKey covers: byAccount


-- *********************************************************************
-- AccountSshKeyAccess
--    @PrimaryKey covers: byAccount, valid


-- *********************************************************************
-- ApprovalCategoryAccess
--    too small to bother indexing


-- *********************************************************************
-- ApprovalCategoryValueAccess
--     @PrimaryKey covers: byCategory


-- *********************************************************************
-- BranchAccess
--    @PrimaryKey covers: byProject


-- *********************************************************************
-- ChangeAccess
--    covers:             byOwnerOpen
CREATE INDEX changes_byOwnerOpen
ON changes (owner_account_id, created_on, change_id)
WHERE open = 'Y';

--    covers:             byOwnerClosed
CREATE INDEX changes_byOwnerClosed
ON changes (owner_account_id, last_updated_on)
WHERE open = 'N';

--    covers:             submitted
CREATE INDEX changes_submitted
ON changes (dest_project_name, dest_branch_name, last_updated_on)
WHERE status = 's';

--    covers:             allOpenPrev, allOpenNext
CREATE INDEX changes_allOpen
ON changes (sort_key)
WHERE open = 'Y';

--    covers:             allClosedPrev, allClosedNext
CREATE INDEX changes_allClosed
ON changes (status, sort_key)
WHERE open = 'N';


-- *********************************************************************
-- ChangeApprovalAccess
--    @PrimaryKey covers: byChange


-- *********************************************************************
-- ChangeMessageAccess
--    @PrimaryKey covers: byChange


-- *********************************************************************
-- ContributorAgreementAccess
--    covers:             active
CREATE INDEX contributor_agreements_active
ON contributor_agreements (active, short_name);


-- *********************************************************************
-- PatchAccess
--    @PrimaryKey covers: byPatchSet


-- *********************************************************************
-- PatchLineCommentAccess
--    @PrimaryKey covers: published, draft
CREATE INDEX patch_comment_drafts
ON patch_comments (author)
WHERE status = 'd';


-- *********************************************************************
-- PatchSetAncestorAccess
--    @PrimaryKey covers: ancestorsOf
--    covers:             descendantsOf
CREATE INDEX patch_set_ancestors_desc
ON patch_set_ancestors (ancestor_revision);


-- *********************************************************************
-- ProjectAccess
--    @PrimaryKey covers: all, suggestByName
--    covers:             ownedByGroup
CREATE INDEX projects_ownedByGroup
ON projects (owner_group_id);


-- *********************************************************************
-- ProjectRightAccess
--    @PrimaryKey covers: byProject
--    covers:             byGroup
CREATE INDEX project_rights_byGroup
ON project_rights (group_id);

--    covers:             byApprovalCategory
CREATE INDEX project_rights_byCat
ON project_rights (category_id);


-- *********************************************************************
-- StarredChangeAccess
--    @PrimaryKey covers: byAccount
