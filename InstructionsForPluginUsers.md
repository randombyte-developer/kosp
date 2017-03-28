# Instructions for plugin users

You were probably sent to this page from a plugin.
That plugin uses some shared code which is also used by some other plugins.
Some instructions for all these plugins are the same, so they are written down here, to be linked by other plugins.

If you have questions please ask me on [my Discord server](https://discord.gg/ZHZ9Z8T).

## Config

The shared code introduces a new way of serialization of:
- [Texts](#texts)
- [TextTemplates](#texttemplates) and
- [Durations](#durations)

### Texts <a name="texts"></a>

Text can be formatted with colors and styles, and some of the TextActions can be set.

#### Color

They are very similar to the [formatting codes](http://minecraft.gamepedia.com/Formatting_codes).
The only difference is that the `§` is replaced by the `&`-symbol.

Examples:
- `&cRed text`
- `&6Gold &2Green`

#### Style

It is the same like with color. Use the [formatting codes](http://minecraft.gamepedia.com/Formatting_codes).

Examples:
- `&nUnderlined text`
- `&oItalic text`

Combined with colors:
- `&c&oRed italic text`
- `&d&lPurple bold text`

#### TextActions

Some TextActions can be expressed using this format which is similar to [Markdown](https://en.wikipedia.org/wiki/Markdown) links:

TextAction | Notation
--- | ---
SuggestCommand(puts it in the chat prompt) | `[Text](&/<cmd>)`
RunCommand | `[Text](/<cmd>)`
OpenUrl | `[Text](<url>)`

Examples:
- `[Click here to buy a lottery ticket](&/lottery buy)`
- `[Click here for good weather](/weather clear)`
- `[Click here to vote](https://www.vote-url.com)`

Combined with color and styles:
- `[&b&lVOTE](https://www.vote-url.com) to be &ccool!`
- `[&6Give everyone a cookie](/give @a minecraft:cookie)`
- <sub>Don't know better examples</sub>

You can see everything can be combined!

Note: The surrounding formatting is not affected by the specific TextAction format:
- `&2Green [&7gray text](/weather clear) still green`

### TextTemplates <a name="texttemplates"></a>

TextTemplates are like Texts but with placeholders. These placeholders can be filled
by the plugin. And as the TextTemplates are written down in the plugin config the user can
easily modify all plugin messages.

TextTemplate parameters are written in curly brackets: `{parameter}`.
The parameter names are set by the plugin. In general, the available parameter names
are listed somewhere(e.g. in the comment of the specific config node).

The plugin inserts a Text object at the place of the parameter. Note that
the plugin can also apply colors, styles and TextActions to the provided Text.

Examples:
- `{playerName} is cool.`
- `The current time is {time}.`

To color and style a parameter you have to write the codes in round brackets before the parameter name in the curly brackets.
Example:
- `A green parameter: {(&2)parameterA}; a parameter without formatting {parameterB}`
- `A purple bold parameter: {(&d&l)parameterA}`

Note: The surrounding formatting is not affected by the specific parameter format:
- `&2Green {(&7)grayParam} still green`

A real world example, taken from the [lottery plugin](https://ore.spongepowered.org/RandomByte/Lottery):
```
# Available parameters: winnerName, pot, currencySymbol, currencyName
draw-message-broadcast="{(&b)winnerName}&6 won the lottery pot of {(&b)pot}{currencySymbol}!"
```
In this example the parameter `currencyName` is not used.
As you can see this can be combined with everything before.

Here is a TextTemplate with everything:
`&c[[Click here]](https://www.vote-url.com) &fto vote for this &lcool server, {(&b)amountVoters} &bpeople &aalready did it!`

### Durations <a name="durations"></a>

A whole other topic. It is just a simple way to express durations.
You get it I think:
- `1h` -> 1 hour
- `1h30m` -> 1 and half an hour
- `12s` -> 12 seconds
- `4d` -> 4 days
- `1d3h9m44s` -> 1 day + 3 hours + 9 minutes + 44 seconds

### Dates <a name="dates"></a>

Self-explaining.

Example:
- "21:23:47 28.03.2017"