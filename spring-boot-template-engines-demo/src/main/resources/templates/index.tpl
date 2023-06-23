yieldUnescaped '<!DOCTYPE html>'
html(lang:'en') {
    head {
        meta('http-equiv':'"Content-Type" content="text/html; charset=utf-8"')
        title("Groovy Demo")
    }
    body {
        h3 ("$message")

        p ("Current date and time: ${new Date()}")

    }
}