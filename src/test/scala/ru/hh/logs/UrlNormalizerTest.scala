package ru.hh.logs

import org.scalatest.FlatSpec
import ru.hh.logs.UrlNormalizer.normalize

class UrlNormalizerTest extends FlatSpec {

  private val idsPlaceholder = "id(s)"

  "normalize" should "return same pathAndQuery if nothing to normalize" in {
    assertResult("/foo/bar")(normalize("/foo/bar"))
  }

  it should "drop query part" in {
    assertResult("/foo")(normalize("/foo?bar"))
  }

  it should s"replace comma separated ids with $idsPlaceholder in the middle of the path" in {
    assertResult(s"/foo/$idsPlaceholder/bar")(normalize("/foo/A-23,45F/bar"))
  }

  it should s"replace comma separated ids with $idsPlaceholder in the end of the path" in {
    assertResult(s"/foo/$idsPlaceholder")(normalize("/foo/A-23,45F"))
  }

  it should s"replace comma separated ids with $idsPlaceholder before query" in {
    assertResult(s"/foo/$idsPlaceholder")(normalize("/foo/123,456?param=value"))
  }

  it should s"not replace A-F letters if they are a part of a path" in {
    assertResult("/FOO/BAR")(normalize("/FOO/BAR"))
  }

  it should "replace // with /" in {
    assertResult("/foo/bar")(normalize("//foo//bar"))
  }

  it should "remove trailing /" in {
    assertResult("/foo/bar")(normalize("/foo/bar/"))
  }

  it should "remove trailing .json" in {
    assertResult("/foo/bar")(normalize("/foo/bar.json"))
  }

  it should "remove trailing .xml" in {
    assertResult("/foo/bar")(normalize("/foo/bar.xml"))
  }

  it should "do all together" in {
    assertResult(s"/foo/$idsPlaceholder/bar/$idsPlaceholder")(normalize("//foo/123,456/bar/123,456//"))
  }

  private val resumeCatalogPagePrefix = "/rs/seo/catalog/resume/page/"

  it should s"replace ${resumeCatalogPagePrefix}prepodavatel with $resumeCatalogPagePrefix$idsPlaceholder" in {
    assertResult(s"$resumeCatalogPagePrefix$idsPlaceholder")(normalize(s"${resumeCatalogPagePrefix}prepodavatel"))
  }

  private val vacancyCatalogPagePrefix = "/rs/seo/catalog/vacancy/page/"

  it should s"replace ${vacancyCatalogPagePrefix}prepodavatel with $vacancyCatalogPagePrefix$idsPlaceholder" in {
    assertResult(s"$vacancyCatalogPagePrefix$idsPlaceholder")(normalize(s"${vacancyCatalogPagePrefix}prepodavatel"))
  }

  private val hhidValidatePrefix = "/hhid/validate/"

  it should s"replace ${hhidValidatePrefix}V2;Tb6 with $hhidValidatePrefix$idsPlaceholder" in {
    assertResult(s"$hhidValidatePrefix$idsPlaceholder")(normalize(s"${hhidValidatePrefix}V2;Tb6"))
  }

  private val accountNotePrefix = "/session/accountNote/"

  it should s"replace ${accountNotePrefix}redesign with $accountNotePrefix$idsPlaceholder" in {
    assertResult(s"$accountNotePrefix$idsPlaceholder")(normalize(s"${accountNotePrefix}redesign"))
  }

  private val browserNotePrefix = "/session/browserNote/"

  it should s"replace ${browserNotePrefix}sendMessageResult526077062 with $browserNotePrefix$idsPlaceholder" in {
    assertResult(s"$browserNotePrefix$idsPlaceholder")(normalize(s"${browserNotePrefix}sendMessageResult526077062"))
  }

  private val sessionNotePrefix = "/session/sessionNote/"

  it should s"replace ${sessionNotePrefix}sudo with $sessionNotePrefix$idsPlaceholder" in {
    assertResult(s"$sessionNotePrefix$idsPlaceholder")(normalize(s"${sessionNotePrefix}sudo"))
  }

  private val userNotePrefix = "/session/userNote/"

  it should s"replace ${userNotePrefix}lang with $userNotePrefix$idsPlaceholder" in {
    assertResult(s"$userNotePrefix$idsPlaceholder")(normalize(s"${userNotePrefix}lang"))
  }

  private val oauthAdminClientsPrefix = "/oauthadmin/clients/"

  it should s"replace ${oauthAdminClientsPrefix}HIOMIA123 with $oauthAdminClientsPrefix$idsPlaceholder" in {
    assertResult(s"$oauthAdminClientsPrefix$idsPlaceholder")(normalize(s"${oauthAdminClientsPrefix}HIOMIA123"))
  }

  val validatePrefix = "/validate/"

  it should s"replace $validatePrefix;http;hh.ru;80;/ with $validatePrefix$idsPlaceholder" in {
    assertResult(s"$validatePrefix$idsPlaceholder")(normalize(s"$validatePrefix;http;hh.ru;80;/"))
  }

  it should s"replace $validatePrefix%3Bhttp%3Bekaterinburg.hh.ru%3B80%3B with $validatePrefix$idsPlaceholder" in {
    assertResult(s"$validatePrefix$idsPlaceholder")(normalize(s"$validatePrefix%3Bhttp%3Bekaterinburg.hh.ru%3B80%3B"))
  }
}
