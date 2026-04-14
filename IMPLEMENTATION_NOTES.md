# Implementation Notes — Session Loop

## Attach/Detach Lifecycle

Every Presenter holds the View as a **nullable reference** (`private var view: View? = null`). Activities call `presenter.attachView(this)` in `onStart()` and `presenter.detachView()` in `onStop()`. This ensures:

- The Presenter never holds a reference to a destroyed Activity, preventing memory leaks.
- Safe calls via `view?.method()` — if the View is gone, the call is a no-op.
- Particularly important for `ActiveSessionActivity`, where a coroutine timer ticks every second. If the Activity is stopped, `detachView()` cancels the coroutine scope, stopping the timer and releasing the View reference.

## Why the Timer Lives in the Presenter

The elapsed-time timer in `ActiveSessionPresenter` uses `kotlinx.coroutines` with a `CoroutineScope` created in `attachView()` and cancelled in `detachView()`. Keeping the timer in the Presenter (not the Activity) means:

- The timer logic is testable without Android framework dependencies.
- The View only receives formatted strings (`"MM:SS"`) — no time math in the Activity.
- The scope lifecycle is explicitly tied to the attach/detach contract, not to Android callbacks.

## What's Stubbed

- **SessionRepository** — in-memory singleton (`object`) backed by a `MutableList<Session>`. No persistence; data is lost on process death. Will be replaced with Room or similar.
- **Navigation after PostSession** — `finish()` returns to the launcher. A home/history screen will replace this.
- **No authentication integration** — the existing Login/Register/Dashboard activities are untouched. PreSessionActivity is the current launcher for development.

---

# Design System & Second-Pass Screens

## Design Token Naming Scheme

Colors, spacing, and component dimensions are defined as Android resources in `res/values/` and referenced exclusively via resource references — no hardcoded hex or dp values in layout XML.

- **Colors** (`colors.xml`): Brand tokens (`accent_orange`, `background_warm`), a `grey_900`–`grey_100` ramp, and semantic aliases (`text_primary`, `text_secondary`, `text_tertiary`, `surface`, `surface_elevated`, `divider`). Legacy names (`accent`, `background`) are kept as aliases pointing to the new brand tokens so existing layouts remain unmodified.
- **Dimensions** (`dimens.xml`): Spacing scale (`space_xs` 4dp through `space_3xl` 48dp), corner radii (`radius_sm` 8dp, `radius_md` 12dp, `radius_lg` 16dp, `radius_pill` 999dp), and standard component heights (`height_button` 56dp, `height_input` 56dp, `height_row` 64dp). Legacy values (`screen_padding`, `button_spacing`) are preserved.
- **Typography** (`styles.xml`): `TextAppearance.Deeply.*` styles (Display, Heading1, Heading2, Body, BodySmall, Caption, Button) using DM Serif Display for Display and DM Sans for everything else. Component styles (`Widget.Deeply.Button.Primary`, `.Secondary`, `.PillTab`, `.TextInputLayout`) ensure consistent component rendering.

## Fonts

DM Sans (UI) and DM Serif Display (display headings) are set up as downloadable fonts via the Google Fonts provider (`res/font/dm_sans.xml`, `dm_sans_medium.xml`, `dm_sans_bold.xml`, `dm_serif_display.xml`). Certificate arrays are in `font_certs.xml`. Fonts are pre-loaded via a `preloaded_fonts` meta-data entry in the manifest. **Fallback:** if downloadable fonts don't resolve on a device without Google Play Services, download the TTF files from Google Fonts and place them in `res/font/`, then update the font XML files to reference the local files instead of the provider.

## Pill Tab Switcher (Login Screen)

The sign-in / sign-up toggle uses a `MaterialButtonToggleGroup` with `singleSelection=true`. The container has a `bg_pill_toggle` drawable (rounded rect with `radius_pill` corners and a subtle border). Each button uses the `Widget.Deeply.Button.PillTab` style which sets `cornerRadius` to `radius_pill`, transparent background, and no stroke. The Activity's `updatePillState()` method imperatively sets the selected button's background to `accent_orange` and text to white, and the unselected button to transparent with `text_secondary`. This approach avoids the complexity of a custom color state list while giving full control over the pill appearance. If we later reuse this pattern, consider extracting the pill state logic into a helper.

## Auth is Stubbed

`AuthRepository` is an in-memory singleton with `signIn()`, `signUp()`, `signInWithGoogle()`, and `signOut()` methods. All auth calls return `Result.success(Unit)` after a 500ms delay. `getCurrentUser()` returns a hardcoded `User(name, email)`. Login success currently navigates to `PreSessionActivity` as a placeholder — a `// TODO: navigate to Dashboard once built` comment marks the spot. No real authentication, token storage, or network calls exist yet.

## Legacy Activities Removed

The old root-package stubs (`LoginActivity`, `ProfileActivity`, `DashboardActivity`, `RegisterActivity`) were deleted. They used `Activity` (not `AppCompatActivity`), `findViewById`, and didn't follow the MVP + vertical slicing pattern. They have been replaced by proper MVP screens under `screens/login/`, `screens/profile/`, and `screens/history/`. Their layout files (`activity_dashboard.xml`, `activity_register.xml`) still exist in `res/layout/` but are orphaned — safe to delete in a cleanup pass.
