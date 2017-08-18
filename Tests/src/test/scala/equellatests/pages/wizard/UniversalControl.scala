package equellatests.pages.wizard

import equellatests.domain.{TestFile, ValidFilename}
import equellatests.pages.BrowserPage
import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.support.ui.{ExpectedCondition, ExpectedConditions}

import scala.util.Try

class UniversalControl(val page: WizardPageTab, val ctrlNum: Int) extends BrowserPage with WizardControl {

  private def actionLinkBy(action: String) = By.xpath("td[@class='actions']/a[text()=" + quoteXPath(action) + "]")

  private def rowForDescription(description: String, disabled: Boolean) = Try(pageElement.findElement(rowDescriptionBy(description, disabled))).toOption

  def editResource[A <: AttachmentEditPage](description: String, condition: ExpectedCondition[A]) : Option[A] = {
    rowForDescription(description, false).map { row =>
      row.findElement(actionLinkBy("Edit")).click()
      waitFor(condition)
    }
  }

  def errorExpectation(msg: String) = {
    ExpectedConditions.visibilityOfNestedElementsLocatedBy(pageBy, By.xpath(s"//p[@class='ctrlinvalidmessage' and text() = ${quoteXPath(msg)}]"))
  }

  def uploadInline[A](tf: TestFile, actualFilename: String, after: ExpectedCondition[A]) : A = {
    elemForId("_fileUpload_file").sendKeys(TestFile.realFile(tf, actualFilename).getAbsolutePath)
    waitFor(after)
  }

  private def rowDescriptionBy(title: String, disabled: Boolean) = By.xpath(".//tr[.//" + (if (disabled) "span" else "a") + "[text()=" + quoteXPath(title) + "]]")

  def attachNameWaiter(description: String, disabled: Boolean): ExpectedCondition[_] = {
    ExpectedConditions.visibilityOfNestedElementsLocatedBy(pageBy, rowDescriptionBy(description, disabled))
  }

  def ctx = page.ctx
  def pageBy = By.id(idFor("universalresources"))

}
