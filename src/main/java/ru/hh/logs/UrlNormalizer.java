package ru.hh.logs;

import java.util.regex.Pattern;

public final class UrlNormalizer {

  /*
  public String normalize(final String url) {
    final String[] pathAndQuery = url.split("\\?");
    String result = pathAndQuery[0];
    result = slashesPattern.matcher(result).replaceAll("/");
    result = stripSuffix(result, "/");
    result = stripSuffix(result, ".xml");
    result = stripSuffix(result, ".json");

    interPath = XMLBackSpecific.normalize(interPath)
    interPath = HHIDSpecific.normalize(interPath)

    return idsPattern.matcher(result).replaceAll(idsReplacement);
  }

  private static final Pattern slashesPattern = Pattern.compile("/{2,}+");

  private static String stripSuffix(final String string, final String suffix) {
    String result = string;
    for (; result.endsWith(suffix); result=result.substring(0, result.length()-suffix.length()));
    return result;
  }

  private static final Pattern idsPattern = Pattern.compile("/[0-9A-F,-]+(/|$)");
  private static final String idsPlaceholder = "id(s)";
  private static final String idsReplacement = '/' + idsPlaceholder + "$1";


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
   */

  private UrlNormalizer() {}
}
