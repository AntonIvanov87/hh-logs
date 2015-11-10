package ru.hh.logs

import java.util.regex.Pattern

private[logs] object UrlNormalizer {

  def normalize(url: String): String = {
    var interPath = url.takeWhile(_ != '?')
    interPath = slashesPattern.matcher(interPath).replaceAll("/")
    interPath = interPath.stripSuffix("/")
      .stripSuffix(".json")
      .stripSuffix(".xml")

    interPath = XMLBackSpecific.normalize(interPath)
    interPath = HHIDSpecific.normalize(interPath)

    idsPattern.matcher(interPath).replaceAll(idsReplacement)
  }

  private val slashesPattern = Pattern.compile("/{2,}+")

  private val idsPattern = Pattern.compile("/[0-9A-F,-]+(/|$)")
  private val idsPlaceholder = "id(s)"
  private val idsReplacement = "/" + idsPlaceholder + "$1"

  private object XMLBackSpecific {

    def normalize(path: String): String = {
      val interPath = resumeCatalogNormalizer.normalize(path)
      vacancyCatalogNormalizer.normalize(interPath)
    }

    private val resumeCatalogNormalizer = new ByPrefixNormalizer("/rs/seo/catalog/resume/page/")
    private val vacancyCatalogNormalizer = new ByPrefixNormalizer("/rs/seo/catalog/vacancy/page/")
  }

  private object HHIDSpecific {

    def normalize(path: String): String = {
      var interPath = hhidValidateNormalizer.normalize(path)
      interPath = accountNoteNormalizer.normalize(interPath)
      interPath = browserNoteNormalizer.normalize(interPath)
      interPath = sessionNoteNormalizer.normalize(interPath)
      interPath = userNoteNormalizer.normalize(interPath)
      interPath = oauthAdminClientsNormalizer.normalize(interPath)
      validateNormalizer.normalize(interPath)
    }

    private val hhidValidateNormalizer = new ByPrefixNormalizer("/hhid/validate/")
    private val accountNoteNormalizer = new ByPrefixNormalizer("/session/accountNote/")
    private val browserNoteNormalizer = new ByPrefixNormalizer("/session/browserNote/")
    private val sessionNoteNormalizer = new ByPrefixNormalizer("/session/sessionNote/")
    private val userNoteNormalizer = new ByPrefixNormalizer("/session/userNote/")
    private val oauthAdminClientsNormalizer = new ByPrefixNormalizer("/oauthadmin/clients/")
    private val validateNormalizer = new ByPrefixNormalizer("/validate/")
  }

  private class ByPrefixNormalizer(prefix: String) {

    private val pattern = Pattern.compile("^" + prefix + ".+")
    private val replacement = prefix + idsPlaceholder

    def normalize(path: String): String = {
      pattern.matcher(path).replaceFirst(replacement)
    }
  }
}
