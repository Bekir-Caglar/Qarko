---
description: Mobile App Campaign & Promotion Integration Guide
---

# Mobile App Campaign Integration Guide

## Overview
This task details the integration of the campaign system into the mobile application, specifically matching the UI structure provided in the `CampaignSelectScreen` Composable. It explains how to fetch data from Firebase, map it to the mobile UI model, and handle the logic for available vs. unavailable campaigns.

## 1. Firebase Data Structure & Path

**Path:** `tenants/{tenantId}/campaigns`
**Query:** Fetch where `validity.isActive == true` AND `validity.endDate >= serverTimestamp()`.

### Firestore Campaign Object (Web/Backend)
```typescript
interface Campaign {
  id: string;
  title: string;       // e.g., "Hafta Sonu Keyfi"
  description: string; // e.g., "Hafta sonu siparişlerinde 25₺ indirim"
  type: 'PERCENTAGE_DISCOUNT' | 'FIXED_DISCOUNT' | ...;
  discountValue: number; // e.g., 20 or 25
  conditions: {
    minOrderAmount: number; // e.g., 100
    requiresCode: boolean;  // true/false
    code?: string;          // e.g., "SUMMER20"
    // Other conditions like applicableItems, applicableHours...
  };
  validity: {
    startDate: Timestamp;
    endDate: Timestamp;
    isActive: boolean;
  };
}
```

## 2. Mobile Data Model Mapping

You need to map the Firestore `Campaign` object to your local `CampaignItem` data class in Kotlin.

### Kotlin Data Class
```kotlin
data class CampaignItem(
    val id: String,
    val title: String,
    val description: String,
    val discountAmount: String, // Derived from type + discountValue
    val isAvailable: Boolean = true, // Calculated based on Cart
    val conditionText: String? = null // Reason why it's not available
)
```

### Mapping Logic
1.  **`discountAmount`**:
    *   If `type == PERCENTAGE_DISCOUNT` -> `"%${discountValue}"`
    *   If `type == FIXED_DISCOUNT` -> `"${discountValue}₺"`
    *   If `type == FREE_ITEM` -> `"1 Ürün Bedava"` (or specific item name if available)

2.  **`isAvailable` & `conditionText`** (The Logic Engine):
    *   You need a UseCase/Helper function: `checkCampaignAvailability(campaign: Campaign, cart: Cart): AvailabilityResult`
    *   **Checks:**
        *   **Min Order Amount:**
            *   Condition: `cart.subtotal >= campaign.conditions.minOrderAmount`
            *   Fail: `isAvailable = false`, `conditionText = "Sepet tutarınız: ${cart.subtotal}₺ (${campaign.conditions.minOrderAmount - cart.subtotal}₺ kaldı)"`
        *   **Code Requirement:**
            *   If `campaign.conditions.requiresCode == true`, this campaign should NOT appear in the "Available" lists automatically. It only appears if the user manually enters the code in the "Campaign Code" section and validates it.
            *   *Exception:* If the user *has* entered the code, treat it like a normal campaign and check other conditions.

## 3. UI Implementation Guide

Based on your `CampaignSelectScreen` code:

### A. Campaign Code Section (`Kampanya Kodun Var Mı?`)
*   **Logic:**
    *   When user clicks "Uygula":
    *   Query Firestore for a campaign with `conditions.code == userInput`.
    *   If found:
        *   Check validity (date, active).
        *   Check other conditions (min amount).
        *   If valid: Apply directly to cart OR add to "Available Campaigns" list as selected.
        *   If invalid: Show error toast (e.g., "Sepet tutarı yetersiz").
    *   If not found: Show error "Geçersiz kod".

### B. Available & Unavailable Lists
Fetch all active campaigns from Firestore. Filter out those with `requiresCode == true` (unless already entered).

Split the rest into two lists:
1.  **`availableCampaigns`**: `checkCampaignAvailability` returns true.
2.  **`unavailableCampaigns`**: `checkCampaignAvailability` returns false. Populate `conditionText` with the reason.

### C. UI Components (Reference to existing code)
*   Use `CampaignSection` composable for both lists.
*   Pass `isDisabled = true` for the unavailable list.
*   Ensure `CampaignItemRow` shows `conditionText` in `primary` color when available/unavailable logic dictates.

## 4. Web Admin Panel Integration (Context)
The Web Admin Panel (`Campaigns.tsx`) has been updated to provide the necessary data fields:
*   **Min. Sepet Tutarı Input:** Populates `conditions.minOrderAmount`.
*   **Kampanya Kodu Switch & Input:** Populates `conditions.requiresCode` and `conditions.code`.
*   **Rules Tab:** Maps to `type` and `discountValue`.

This ensures that when a restaurant owner creates a "100₺ Üzeri Kargo Bedava" campaign in the web panel, it correctly populates the JSON that your mobile app consumes to show the "Koşulu Sağlamak Gereken Kampanyalar" section.
