package app.data.auth;

import app.data.ContentItem;

public record AdminPermission(UserType adminType, ContentItem contentItem) {
}
