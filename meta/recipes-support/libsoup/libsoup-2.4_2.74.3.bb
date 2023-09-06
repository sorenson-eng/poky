SUMMARY = "An HTTP library implementation in C"
DESCRIPTION = "libsoup is an HTTP client/server library for GNOME. It uses GObjects \
and the glib main loop, to integrate well with GNOME applications."
HOMEPAGE = "https://wiki.gnome.org/Projects/libsoup"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome/libs"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "glib-2.0 glib-2.0-native libxml2 sqlite3 libpsl"

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI = "${GNOME_MIRROR}/libsoup/${SHRT_VER}/libsoup-${PV}.tar.xz"
SRC_URI[sha256sum] = "e4b77c41cfc4c8c5a035fcdc320c7bc6cfb75ef7c5a034153df1413fa1d92f13"

CVE_PRODUCT = "libsoup"

S = "${WORKDIR}/libsoup-${PV}"

inherit meson gettext pkgconfig upstream-version-is-even gobject-introspection gtk-doc

UPSTREAM_CHECK_REGEX = "libsoup-(?P<pver>2(\.(?!99)\d+)+)\.tar"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

PACKAGECONFIG ??= ""
PACKAGECONFIG[brotli] = "-Dbrotli=enabled,-Dbrotli=disabled,brotli"
# libsoup-gnome is entirely deprecated and just stubs in 2.42 onwards
PACKAGECONFIG[gnome] = "-Dgnome=true,-Dgnome=false"
PACKAGECONFIG[gssapi] = "-Dgssapi=enabled,-Dgssapi=disabled,krb5"
PACKAGECONFIG[ntlm] = "-Dntlm=enabled,-Dntlm=disabled"
PACKAGECONFIG[sysprof] = "-Dsysprof=enabled,-Dsysprof=disabled,sysprof"

# Tell libsoup where the target ntlm_auth is installed
do_write_config:append:class-target() {
    cat >${WORKDIR}/soup.cross <<EOF
[binaries]
ntlm_auth = '${bindir}/ntlm_auth'
EOF
}
EXTRA_OEMESON += "--cross-file ${WORKDIR}/soup.cross"

EXTRA_OEMESON += "-Dvapi=disabled -Dtls_check=false"

GTKDOC_MESON_OPTION = "gtk_doc"

# When built without gnome support, libsoup-2.4 will contain only one shared lib
# and will therefore become subject to renaming by debian.bbclass. Prevent
# renaming in order to keep the package name consistent regardless of whether
# gnome support is enabled or disabled.
DEBIAN_NOAUTONAME:${PN} = "1"

# glib-networking is needed for SSL, proxies, etc.
RRECOMMENDS:${PN} = "glib-networking"

BBCLASSEXTEND = "native nativesdk"
